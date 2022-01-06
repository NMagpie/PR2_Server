package pr.security

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import pr.config
import pr.database.User
import pr.database.findByUsername
import pr.database.updateActivation
import java.util.*

private val codes = Collections.synchronizedMap<String, String>(HashMap())

private val chars = 0..9

fun Application.activate() {
    routing {
        authenticate("auth-digest") {
            get("/activate") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null)
                    call.respondText("nope")
                else {
                    val user: User? = findByUsername(principal.name)
                    if (user != null) {
                        val code = (1..6).map { chars.random() }.joinToString("")
                        codes[user.username] = code
                        call.respondText("okay")
                        sendEmail(code, user.email)
                    } else
                        call.respondText("nope")
                }
            }

            get("/activate/code") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal == null)
                    call.respondText("nope")
                else {
                    val user: User? = findByUsername(principal.name)
                    if (user != null) {
                        val code = call.request.queryParameters["code"]
                        if (codes[user.username] == code) {
                            codes.remove(user.username)
                            updateActivation(user.username)
                            call.respondText("okay")
                        } else
                            call.respondText("nope")
                    } else
                        call.respondText("nope")
                }
            }

        }
    }
}

fun sendEmail(code: String, emailAddress: String) {
    val email = SimpleEmail()
    email.hostName = "smtp.googlemail.com"
    email.setSmtpPort(465)
    email.setAuthenticator(DefaultAuthenticator(config.email, config.password))
    email.isSSLOnConnect = true
    email.setFrom("PRLab@gmail.com")
    email.subject = "Account activation"
    email.setMsg("Hello, user!\nYou have requested activation code for your account.\nYour code is: $code\nThank you!")
    email.addTo(emailAddress)
    email.send()
}