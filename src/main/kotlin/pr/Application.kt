package pr

import io.ktor.application.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pr.chat.chat
import pr.database.initDB
import pr.security.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

@Serializable
data class Config(var email: String = "", var password: String = "")

var config = Config()

val pathConfig = System.getProperty("user.dir") + "\\config.json"

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
// application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    initCrypto()
    initDB()
    auth()
    chat()
    activate()
}

fun updateConfig() {
    setKey()
    print("Email: ")
    var input = readLine()
    if (input != null) {
        config.email = encrypt(input)
    }

    print("Password: ")
    input = readLine()
    if (input != null) {
        config.password = encrypt(input)
    }

    val jsonMapper = Json {
        prettyPrint = true
    }

    val json = jsonMapper.encodeToString(config)
    Files.write(Paths.get(pathConfig), json.toByteArray(Charset.defaultCharset()))
}