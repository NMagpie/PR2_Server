package pr

import io.ktor.application.*
import pr.chat.chat
import pr.database.initDB
import pr.security.auth

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    initDB()
    auth()
    chat()
}

//val users : List<User> = usersCollection.find(User::username eq "Test123").toList()