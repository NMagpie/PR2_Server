package pr.http

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*

fun Application.http() {
    routing {
        authenticate("auth-digest") {
            get("/activate") {

            }
        }
    }
}