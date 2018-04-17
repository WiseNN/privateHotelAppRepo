package kots;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.protocol.PacketType;
import couchdb.RestaurantItem;
import devutil.ConsoleColors;
import devutil.MyUtil;
import io.socket.parser.Packet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.PriorityBlockingQueue;


 public class KOTS_Server_Java
{

    private static int PORT = 7000;
    private static SocketIOClient uiSocket = null;
    private static SocketIOServer server = null;

      static ConcurrentLinkedQueue<KOTS_Order> orderQueue = new ConcurrentLinkedQueue<KOTS_Order>();
     static ConcurrentHashMap<String, SocketIOClient> terminalSocketMap  = new ConcurrentHashMap<String, SocketIOClient>();

    static KitchenOrderQueueView kitchView  = null;
//    static Timer timer = new Timer();


    KOTS_Server_Java()
    {



    }

    public static void main(String[] args)
    {




    }

    static void initDequeueTask()
    {


//        TimerTask task = new TimerTask() {
//            int i = 0;
//            @Override
//            public void run() {

                System.out.println("counting...");

                if(KOTS_Server_Java.orderQueue.peek() != null)
                {
                    //if available, send the next order to the kitchen view screen
                    kitchView.updateOrders(KOTS_Server_Java.orderQueue.poll());
                }


//            }
//        };

//        timer.schedule(task, 2000, 540000L);
//        timer.schedule(task, 100, 2000);
    }

    static void startServer() throws InterruptedException
    {
        //get host address to connect to
        InetAddress ipInfo = null;
        try{
            ipInfo = InetAddress.getLocalHost();
        }catch(UnknownHostException e)
        {
            System.out.println(e.getMessage());
        }


        Configuration config = new Configuration();
        config.setHostname(ipInfo.getHostAddress());
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
        server.addEventListener(EventNames.addOrder, String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String serializedKOTS_Order, AckRequest ackRequest) {

                KOTS_Order newKotsOrder = null;


                try{
                        newKotsOrder =  new MyUtil().deserializeObject(KOTS_Order.class, serializedKOTS_Order);
                }catch(Exception e)
                {
                 System.out.println(e.getMessage());
                }

                if (KOTS_Server_Java.orderQueue != null)
                {
                    if(newKotsOrder != null)
                    {
                        //added new order
                        orderQueue.add(newKotsOrder);
                        ackRequest.sendAckData(200);
                    }else{
                        //new order was null
                        ackRequest.sendAckData(400);
                    }
                }

                else
                {

                    System.out.println(ConsoleColors.cyanText("THIS ORDER QUEUE HAS NOT BEEN CREATED YET CANNOT ADD ITEM!!!!"));
                    //send back false to tell sender this action was not taken (order was not added to queue)
                    ackRequest.sendAckData(500);
                }





//                //get the restuarantItem from data param cast to RestuarantItem
//                RestaurantItem restuarantItem = (RestaurantItem) data.get("item");
//
//                if (orderQueue != null)
//                {
//                    orderQueue.get().add(restuarantItem);
//                    ackRequest.sendAckData(false);
//                }
//                else
//                {
//                    System.out.println(ConsoleColors.cyanText("THIS ORDER QUEUE HAS NOT BEEN CREATED YET CANNOT ADD ITEM!!!!"));
//                    //send back false to tell sender this action was not taken (order was not added to queue)
//                    ackRequest.sendAckData(false);
//                }
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


        //display IP Address to connect to from local LAN/WAN
        kitchView.getConnectedStatusText().setText(ipInfo.getHostAddress()+":"+PORT);
        kitchView.getStatusIndicator().setFill( Color.web("#12ff00"));

        System.out.println("LoopBack Address: ${InetAddress.getLoopbackAddress().hostAddress}");

//        initDequeueTask();

    }

    static void stopServer()
    {

        server.stop();
        kitchView.getConnectedStatusText().setText("Not Connected...");
        kitchView.getStatusIndicator().setFill(Color.web("#ff0000"));


    }


    void initOrderQueue()
    {

//        orderQueue.addListener((observable, oldValue, newValue) -> {
//        System.out.println("textfield changed from " + oldValue + " to " + newValue);
//    });


    }
}
