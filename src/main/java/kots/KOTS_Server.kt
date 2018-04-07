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
import org.json.JSONObject


object KOTS_Server
{


    private val PORT = 5000
    internal var server: SocketIOServer? = null
    private val orderQueue  = SimpleObjectProperty<Queue<RestaurantItem>>()
    private var uiSocket : SocketIOClient? = null
    private var terminalSocketMap  = HashMap<String, SocketIOClient>()


    @Throws(InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>)
    {
        val ts = Thread(Runnable {
            try {
                initOrderQueue()
                server()

            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

        })

        ts.start()
    }



    fun initOrderQueue()
    {

        orderQueue.addListener { observable, oldValue, newValue ->
            //call UI and update the host UI and update the prompt

        }
    }



    @Throws(InterruptedException::class, UnsupportedEncodingException::class)

    fun server() {
        val config = Configuration()
        config.setHostname("localhost")
        config.setPort(PORT)
        server = SocketIOServer(config)


        //add orders to the queue
        server!!.addEventListener("addOrder", JSONObject::class.java) { client, data, ackRequest ->

            //get the restuarantItem from data param cast to RestuarantItem
            val restuarantItem = data["item"] as RestaurantItem

            if (orderQueue != null)
            {
                orderQueue!!.get().add(restuarantItem)
            }
            else
            {
                System.out.println(ConsoleColors.cyanText("THIS ORDER QUEUE HAS NOT BEEN CREATED YET CANNOT ADD ITEM!!!!"))
                //send back false to tell sender this action was not taken (order was not added to queue)
                ackRequest.sendAckData(false)
            }
        }


        server!!.addEventListener("connect", JSONObject::class.java) { client, data, ackRequest ->


            if (data["clientID"] == "UI_SOCKET") {
                //not sure what the [] dictionary param is for, guess we'll find out

                //store uiSocket reference
                uiSocket!!["uiClient"] = client

                println("UI HOST client connected!")

            }
            else
            {
                println("terminal client connected!")
                val obj = data

                //retain client in dictionary
                terminalSocketMap[data["clientID"] as String] = client
            }


        }
        server!!.addEventListener("dequeueItem", String::class.java) { client, data, ackRequest ->

            val p = com.corundumstudio.socketio.protocol.Packet(PacketType.EVENT)


            //if items exist in the orderQueue, send back an item, else ignore request
            if (orderQueue.get().size > 0) {
                p.setData(orderQueue.get().remove())

                client.send(p)
            }


            server!!.addDisconnectListener {

                println("disconnecting....")
//            Thread.sleep(20000)
                println("disconnected!!!!")

            }


            server!!.addConnectListener {

                println("connect from server")
            }

            server!!.start()
        }
    }
}

