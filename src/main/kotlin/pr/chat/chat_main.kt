package pr.chat

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Application.chat() {
    install(WebSockets)
    routing {
        authenticate("auth-digest") {
            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            webSocket("/chat") {

                val principal = call.principal<UserIdPrincipal>()!!

                println("Adding user!")
                val thisConnection = Connection(this, principal.name)
                connections += thisConnection
                try {
                    send("You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("${thisConnection.name} has left!")
                    connections -= thisConnection
                }
            }
        }
    }
}

class Connection(val session: DefaultWebSocketSession, username: String) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = lastId.getAndIncrement()

    val name = username
}