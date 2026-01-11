package com.inrotate

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "1") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}