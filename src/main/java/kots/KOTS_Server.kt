package kots

import com.corundumstudio.socketio.*
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.protocol.PacketType
import io.socket.client.IO
import io.socket.client.Socket
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import java.io.PrintWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import couchdb.RestaurantItem
import devutil.ConsoleColors
import io.socket.engineio.parser.Packet
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import tornadofx.*
import com.corundumstudio.socketio.AckCallback
import devutil.MyUtil
import javafx.scene.paint.Color
import org.json.JSONObject
import java.net.InetAddress
import tornadofx.*
import kotlin.collections.HashMap
import kotlin.reflect.jvm.javaGetter

object KOTS_Server
{



    internal var server: SocketIOServer? = null
    private val PORT = 5000
     val orderQueue  = SimpleObjectProperty<Queue<KOTS_Order>>()
    private var uiSocket : SocketIOClient? = null
    private var terminalSocketMap  = HashMap<String, SocketIOClient>()
    var kitchView : KitchenOrderQueueView? = null

    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>)
    {
////        val ts = Thread(Runnable {
//            try {
//                initOrderQueue()
//                server()
//
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//
////        })

//        ts.start()
    }


    fun startServer()
    {


        //        val ts = Thread(Runnable {
        try {


            initOrderQueue()
            server()

        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

//        })

//        ts.start()
    }

    fun stopServer()
    {
        server!!.stop()
        kitchView!!.connectedStatusText.text = "Not Connected..."
        kitchView!!.statusIndicator.fill = Color.web("#ff0000")

    }

    fun initOrderQueue()
    {

        orderQueue.addListener { observable, oldValue, newValue ->
            //call UI and update the host UI and update the prompt

        }
    }



    @Throws(InterruptedException::class, UnsupportedEncodingException::class)

    fun server()
    {
        val config = Configuration()
        config.setHostname("localhost")
        config.setPort(PORT)
        server = SocketIOServer(config)

        server!!.addConnectListener {
            println("Hello World!")
        }



        //add orders to the queue
        server!!.addEventListener(EventNames.addOrder,String::class.java) { client,serializedKOTS_Order, ackRequest ->

            //get the restuarantItem from data param cast to RestuarantItem


                val newKotsOrder =  MyUtil().deserializeObject(KOTS_Order::class.java, serializedKOTS_Order)



            if (orderQueue != null)
            {
                orderQueue!!.get().add(newKotsOrder)
                ackRequest.sendAckData(true)
            }
            else
            {
                System.out.println(ConsoleColors.cyanText("THIS ORDER QUEUE HAS NOT BEEN CREATED YET CANNOT ADD ITEM!!!!"))
                //send back false to tell sender this action was not taken (order was not added to queue)
                ackRequest.sendAckData(false)
            }
        }

        server!!.addEventListener("chatevent", String::class.java) { client, textData, ackRequest ->
            // broadcast messages to all clients
            server!!.getBroadcastOperations().sendEvent("chatevent", textData)
        }



        server!!.addEventListener("dequeueOrder", String::class.java) { client, data, ackRequest ->

            val p = com.corundumstudio.socketio.protocol.Packet(PacketType.EVENT)


            //if items exist in the orderQueue, send back an item, else ignore request
            if (orderQueue.get().size > 0) {
                p.setData(orderQueue.get().remove())

                client.send(p)
            }




            server!!.addEventListener(EventNames.requestClientSignOn, String::class.java) { client, data, ackRequest ->

                println(EventNames.requestClientSignOn)

                val isEncryptedPassword = KOTS_EmployeeManager().getKOTS_User(KOTS_EmployeeManager.kotsUserType.CLIENT, data)

                if(isEncryptedPassword != null)
                {

                    client.sendEvent(EventNames.signOn, isEncryptedPassword)
                }else{
                    client.sendEvent(EventNames.signOn,"no user")
                }
            }


//                //if items exist in the orderQueue, send back an item, else ignore request
//                if (orderQueue.get().size > 0) {
//                    p.setData(orderQueue.get().remove())
//
//                    client.send(p)
//                }

        }
        server!!.addDisconnectListener {

            println("disconnecting....")
//            Thread.sleep(20000)
            println("disconnected!!!!")

        }

        server!!.addConnectListener {

            println("client connected!! client: $it")
        }

        server!!.start()
        println("Server Online...")
        println("Local Address: ${InetAddress.getLocalHost().hostAddress}")
        //display IP Address to connect to from local LAN/WAN
        kitchView!!.connectedStatusText.text = InetAddress.getLocalHost().hostAddress+":$PORT"
        kitchView!!.statusIndicator.fill = Color.web("#12ff00")

        println("LoopBack Address: ${InetAddress.getLoopbackAddress().hostAddress}")
    }
}

