package com.inrotate

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.Database
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    fun Application.configureTestDatabases() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver", user = "root", password = "")
    }

    @Test
    fun testRoot() = testApplication {
        application {
            configureTestDatabases()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

}
