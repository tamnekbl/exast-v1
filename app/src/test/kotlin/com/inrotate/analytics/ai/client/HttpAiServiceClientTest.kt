package com.inrotate.analytics.ai.client

import com.inrotate.analytics.AiServiceBadResponseException
import com.inrotate.analytics.AiServiceConfig
import com.inrotate.analytics.AiServiceTimeoutException
import com.inrotate.analytics.AiServiceUnavailableException
import com.inrotate.analytics.ai.dto.AiPredictionRequest
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HttpAiServiceClientTest {
    @Test
    fun `predict sends request to python endpoint and parses response`() = runBlocking {
        var capturedRequest: HttpRequestData? = null
        val client = testClient(
            MockEngine { request ->
                capturedRequest = request
                respond(
                    content = """
                        {
                          "predicted_participants": 123.5,
                          "model_version": "v1",
                          "model_trained_at": "2026-05-24T10:00:00",
                          "metrics": {"mae": 2.1},
                          "warnings": []
                        }
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val response = client.predict(testPredictionRequest())

        assertEquals(HttpMethod.Post, capturedRequest?.method)
        assertEquals("/predict-attendance", capturedRequest?.url?.encodedPath)
        assertEquals(123.5, response.predictedParticipants)
        assertEquals("v1", response.modelVersion)
    }

    @Test
    fun `predict maps 500 response to unavailable exception`() = runBlocking {
        val client = testClient(
            MockEngine {
                respondError(HttpStatusCode.InternalServerError, "boom")
            },
        )

        assertFailsWith<AiServiceUnavailableException> {
            client.predict(testPredictionRequest())
        }
    }

    @Test
    fun `predict maps timeout to timeout exception`() = runBlocking {
        val client = testClient(
            MockEngine {
                delay(100)
                respond("""{}""")
            },
            requestTimeoutMillis = 1,
        )

        assertFailsWith<AiServiceTimeoutException> {
            client.predict(testPredictionRequest())
        }
    }

    @Test
    fun `predict maps invalid json to bad response exception`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """{"unexpected": true}""",
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        assertFailsWith<AiServiceBadResponseException> {
            client.predict(testPredictionRequest())
        }
    }

    @Test
    fun `predict serializes request using snake case fields`() = runBlocking {
        var body = ""
        val client = testClient(
            MockEngine { request ->
                body = (request.body as OutgoingContent.ByteArrayContent)
                    .bytes()
                    .decodeToString()
                respond(
                    content = """
                        {"predicted_participants":1.0,"model_version":"v1","model_trained_at":null,"metrics":null,"warnings":[]}
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        client.predict(testPredictionRequest())

        assertTrue(body.contains("date_start"))
        assertTrue(body.contains("organization_role"))
        assertTrue(body.contains("time_start"))
        assertTrue(body.contains("dateStart").not())
    }

    private fun testClient(
        engine: MockEngine,
        requestTimeoutMillis: Long = 30_000,
    ): HttpAiServiceClient {
        val config = AiServiceConfig(
            baseUrl = "http://ai.local",
            connectTimeoutMillis = 5_000,
            requestTimeoutMillis = requestTimeoutMillis,
            socketTimeoutMillis = 30_000,
            enabled = true,
        )
        val httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
            install(HttpTimeout) {
                this.requestTimeoutMillis = config.requestTimeoutMillis
                this.connectTimeoutMillis = config.connectTimeoutMillis
                this.socketTimeoutMillis = config.socketTimeoutMillis
            }
        }

        return HttpAiServiceClient(config, httpClient)
    }

    private fun testPredictionRequest(): AiPredictionRequest = AiPredictionRequest(
        title = "Event",
        description = null,
        dateStart = "2026-06-01",
        dateEnd = null,
        timeStart = "10:00",
        timeEnd = null,
        level = "university",
        format = "offline",
        organizationRole = "organization",
        types = listOf("cultural_creative"),
        organizations = emptyList(),
    )
}
