package com.inrotate.api

import com.inrotate.db.users.User
import com.inrotate.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.configureUsers(userRepository: UserRepository) {
    route("/db/users") {
        get {
            val name = call.request.queryParameters["name"]

            val users = userRepository.getFiltered(name)
            if (users.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, users)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }  //todo валидация id
            val user = userRepository.getById(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(user)
        }

        post {
            try {
                val user = call.receive<User>()
                userRepository.add(user)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("User added successfully", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(e.message, false)
                )
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Invalid or missing ID", false)
                )
                return@put
            }

            userRepository.getById(id)
                ?: return@put call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No user with id $id", false)
                )

            try {
                val updatedUser = call.receive<User>() // Получаем объект из тела запроса

                userRepository.edit(id, updatedUser) // Вызываем функцию обновления
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("User updated successfully", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse(e.message, false)
                )
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse("Invalid or missing ID", false)
                )
                return@delete
            }

            userRepository.getById(id)
                ?: return@delete call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse("No user with id $id", false)
                )

            try {
                userRepository.remove(id)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse("User deleted correctly", true)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse(e.message, false)
                )
            }
        }
    }
}
