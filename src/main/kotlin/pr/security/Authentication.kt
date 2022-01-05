package pr.security

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import pr.database.findByUsername
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8
import kotlinx.serialization.json.Json
import pr.database.User

fun getMd5Digest(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

fun Application.auth() {
    install(Authentication) {
        digest("auth-digest") {
            realm = "login-user"
            digestProvider { userName, realm ->
                val user = findByUsername(userName)
                getMd5Digest("${userName}:$realm:${user?.password}")
            }
        }
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
        })
    }

    routing {
        authenticate("auth-digest") {
            get("/login") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null)
                    call.respondText("nope")
                else {
                    val user : User? = findByUsername(principal.name)
                    if (user != null) {
                    user.password = ""
                    call.respond(user)
                    }
                }
            }
        }
    }
}