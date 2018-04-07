package kots

import io.javalin.Javalin

class KOTS_Server2
{


    init {
        setupSocketServer()
    }



    fun setupSocketServer()
    {
        val app = Javalin.start(7000)

   app.ws("/") { ws ->

    ws.onConnect { session -> println("Connected") }


    ws.onMessage { session, message ->

        println("Received: " + message)



        session.remote.sendString("Echo: " + message)
    }



    ws.onClose { session, statusCode, reason -> println("Closed") }


    ws.onError { session, throwable -> println("Errored") }

}

    }
}
