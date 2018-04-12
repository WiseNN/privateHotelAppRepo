package kots

import io.javalin.Javalin
import j2html.TagCreator.*
import org.eclipse.jetty.websocket.api.Session
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class KOTS_Server2
{

    private val userUsernameMap = ConcurrentHashMap<Session, String>()
    private var nextUserNumber = 1 // Assign to username for next connecting user

    fun main(args: Array<String>) {

        Javalin.create().apply {
            port(7070)
//            enableStaticFiles("/public")
            ws("/") { ws ->
                ws.onConnect { session ->
                    val username = "User" + nextUserNumber++
                    userUsernameMap.put(session, username)
                    broadcastMessage("Server", username + " joined the chat")
                }
                ws.onClose { session, status, message ->
                    val username = userUsernameMap[session]
                    userUsernameMap.remove(session)
                    broadcastMessage("Server", username + " left the chat")
                }
                ws.onMessage { session, message ->
                    broadcastMessage(userUsernameMap[session]!!, message)
                }
            }
        }.start()
    }

    // Sends a message from one user to all users, along with a list of current usernames
    fun broadcastMessage(sender: String, message: String) {
        userUsernameMap.keys.filter { it.isOpen }.forEach { session ->
            session.remote.sendString(
                    JSONObject()
                            .put("userMessage", createHtmlMessageFromSender(sender, message))
                            .put("userlist", userUsernameMap.values).toString()
            )
        }
    }

    // Builds a HTML element with a sender-name, a message, and a timestamp,
    private fun createHtmlMessageFromSender(sender: String, message: String): String {
        return article(
                b(sender + " says:"),
                span(attrs(".timestamp"), SimpleDateFormat("HH:mm:ss").format(Date())),
                p(message)
        ).render()
    }
}
