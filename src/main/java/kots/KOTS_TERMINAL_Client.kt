package kots

import com.corundumstudio.socketio.SocketIOClient
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


class KOTS_TERMINAL_Client
{
    private var socket: Socket? = null
    private var PORT = 5000
    @Throws(URISyntaxException::class, InterruptedException::class)


    fun main()
    {
        try {
            client()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    fun client()
    {

        socket = IO.socket("http://localhost:$PORT")

        socket!!.on(Socket.EVENT_CONNECT) {
            socket!!.emit("toServer", "connected")
        }


        socket!!.on("orderUpdate") { data ->

            //event will get called when there is a status change on an order
            println("order Update: "+data[0].toString())

        }
        socket!!.connect()
        var i = 0
        while (!socket!!.connected())
        {
            i = i % 50
            i++
            println("waiting to connect $i")
            Thread.sleep(50)
        }

        socket!!.disconnect()
    }

}