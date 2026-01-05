package com.inrotate

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

//todo в респонсе выводить все поля null+
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true  // игнорировать неизвестные поля
                explicitNulls = false  // Для входящих
            }
        )
    }
}
