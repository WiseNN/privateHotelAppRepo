package kots;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.protocol.PacketType;
import couchdb.RestaurantItem;
import devutil.ConsoleColors;
import io.socket.parser.Packet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class KOTS_Server_Java
{

    private static int PORT = 7000;
    private static SocketIOClient uiSocket = null;
    private static SocketIOServer server = null;

    private  static SimpleObjectProperty<ConcurrentLinkedQueue<RestaurantItem>> orderQueue  = new SimpleObjectProperty< ConcurrentLinkedQueue<RestaurantItem>>();
    private static ConcurrentHashMap<String, SocketIOClient> terminalSocketMap  = new ConcurrentHashMap<String, SocketIOClient>();

    static KitchenOrderQueueView kitchView  = null;



    public static void main(String[] args)
    {


    }

    static void startServer() throws InterruptedException
    {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(PORT);

         server = new SocketIOServer(config);
        //user will request to signOn, we will check if username exists, if so we send back
        //encrypted password, user will sign back sign on event if successfully authenticated
        server.addEventListener(EventNames.requestClientSignOn, String.class, new DataListener<String>() {


            @Override
            public void onData(SocketIOClient client, String username, AckRequest ackRequest) {


                String isEncryptedPassword = new KOTS_EmployeeManager().getKOTS_User(KOTS_EmployeeManager.kotsUserType.CLIENT, username);

                if(isEncryptedPassword != null)
                {
                    //send back ack with encrypted password
                    ackRequest.sendAckData(isEncryptedPassword);

                }else{
                    //send back ack with no user string
                    ackRequest.sendAckData("no user");
                }
            }

        });

        //after user authenticates locally, we will add them to the list of currently online terminal devices
        server.addEventListener(EventNames.signOn, String.class, new DataListener<String>() {

            @Override
            public void onData(SocketIOClient client, String username, AckRequest ackRequest) {

                terminalSocketMap.put(username,client);
                ackRequest.sendAckData(true);
            }

        });


        //this event listener adds orders to the queue, and sends back an ack to the user
        server.addEventListener(EventNames.addOrder, JSONObject.class, new DataListener<JSONObject>() {
            @Override
            public void onData(SocketIOClient client, JSONObject data, AckRequest ackRequest) {

                //get the restuarantItem from data param cast to RestuarantItem
                RestaurantItem restuarantItem = (RestaurantItem) data.get("item");

                if (orderQueue != null)
                {
                    orderQueue.get().add(restuarantItem);
                    ackRequest.sendAckData(false);
                }
                else
                {
                    System.out.println(ConsoleColors.cyanText("THIS ORDER QUEUE HAS NOT BEEN CREATED YET CANNOT ADD ITEM!!!!"));
                    //send back false to tell sender this action was not taken (order was not added to queue)
                    ackRequest.sendAckData(false);
                }
            }
        });

        server.addEventListener(EventNames.dequeueOrder, JSONObject.class, new DataListener<JSONObject>() {
            @Override
            public void onData(SocketIOClient client, JSONObject data, AckRequest ackRequest) {


                //get the restuarantItem from data param cast to RestuarantItem
                RestaurantItem restuarantItem = (RestaurantItem) data.get("item");


            }
        });


        server.start();
        System.out.println("Server Online...");
        InetAddress ipInfo = null;

        //get host
        try{
            ipInfo = InetAddress.getLocalHost();
        }catch(UnknownHostException e)
        {
            System.out.println(e.getMessage());
        }

        //display IP Address to connect to from local LAN/WAN
        kitchView.getConnectedStatusText().setText(ipInfo.getHostAddress()+":"+PORT);
        kitchView.getStatusIndicator().setFill( Color.web("#12ff00"));

        System.out.println("LoopBack Address: ${InetAddress.getLoopbackAddress().hostAddress}");

    }

    static void stopServer()
    {
        server.stop();
        kitchView.getConnectedStatusText().setText("Not Connected...");
        kitchView.getStatusIndicator().setFill(Color.web("#ff0000"));

    }

    void initOrderQueue()
    {

        orderQueue.addListener((observable, oldValue, newValue) -> {
        System.out.println("textfield changed from " + oldValue + " to " + newValue);
    });


    }
}
