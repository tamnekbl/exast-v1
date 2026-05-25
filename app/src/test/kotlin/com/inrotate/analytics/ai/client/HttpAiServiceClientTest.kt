package com.inrotate.analytics.ai.client

import com.inrotate.analytics.*
import com.inrotate.analytics.ai.dto.AiEventScalePredictionRequest
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
                          "predictedScale": "large_51_200",
                          "description": "Крупное мероприятие: 51-200 участников",
                          "participantsRange": "51-200",
                          "probabilities": {"small_1_20": 0.12, "large_51_200": 0.51},
                          "confidence": 0.51,
                          "similarEvents": [
                            {
                              "title": "Historical event",
                              "description": "Similar",
                              "dateStart": "2023-05-02",
                              "dateEnd": "2023-05-02",
                              "level": "university",
                              "format": "offline",
                              "organizationRole": "organization",
                              "mainType": "cultural_creative",
                              "mainOrganizationType": "OTHER",
                              "participantsTotal": 2624,
                              "eventScale": "mass_201_plus",
                              "similarity": 0.86
                            }
                          ],
                          "modelVersion": "v1",
                          "modelTrainedAt": "2026-05-24T10:00:00",
                          "metrics": {"accuracy": 0.5},
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
        assertEquals("large_51_200", response.predictedScale)
        assertEquals(0.51, response.confidence)
        assertEquals(0.51, response.probabilities.getValue("large_51_200"))
        assertEquals("Historical event", response.similarEvents.single().title)
        assertEquals("mass_201_plus", response.similarEvents.single().eventScale)
        assertEquals("v1", response.modelVersion)
    }

    @Test
    fun `train parses metrics with nested classification report`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """
                        {
                          "modelVersion": "event_scale_v1",
                          "trainedAt": "2026-05-24T12:38:44",
                          "metrics": {
                            "accuracy": 0.46,
                            "balanced_accuracy": 0.47,
                            "classification_report": {
                              "small_1_20": {"precision": 0.5}
                            }
                          },
                          "baselineMetrics": {"accuracy": 0.33},
                          "warnings": [],
                          "featureSchema": ["level"],
                          "classLabels": ["small_1_20"],
                          "classDescriptions": {"small_1_20": "small"}
                        }
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val response = client.train("csv".toByteArray())

        assertEquals("event_scale_v1", response.modelVersion)
        assertTrue(response.metrics.containsKey("classification_report"))
    }

    @Test
    fun `predict maps 500 response to prediction failed exception with python message`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """{"error":"PREDICTION_FAILED","message":"Prediction failed.","details":{}}""",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val exception = assertFailsWith<AiPredictionFailedException> {
            client.predict(testPredictionRequest())
        }
        assertEquals("Prediction failed.", exception.message)
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
    fun `predict maps model not found to model not found exception`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """{"error":"MODEL_NOT_FOUND","message":"No trained models found in model registry.","details":{}}""",
                    status = HttpStatusCode.NotFound,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        assertFailsWith<AiModelNotFoundException> {
            client.predict(testPredictionRequest())
        }
    }

    @Test
    fun `predict maps bad request to bad request exception`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """{"error":"REQUEST_VALIDATION_ERROR","message":"Request validation failed.","details":{}}""",
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        assertFailsWith<AiBadRequestException> {
            client.predict(testPredictionRequest())
        }
    }

    @Test
    fun `getLatestModel parses snake case model metadata`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """
                        {
                          "model_version": "event_scale_v1",
                          "trained_at": "2026-05-24T12:38:44",
                          "metrics": {"accuracy": 0.46},
                          "baseline_metrics": {"accuracy": 0.33},
                          "warnings": []
                        }
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val model = client.getLatestModel()

        assertEquals("event_scale_v1", model.modelVersion)
        assertEquals("2026-05-24T12:38:44", model.trainedAt)
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
                        {"predictedScale":"large_51_200","description":"large","participantsRange":"51-200","probabilities":{"large_51_200":1.0},"confidence":1.0,"similarEvents":[],"modelVersion":"v1","modelTrainedAt":null,"metrics":{},"warnings":[]}
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        client.predict(testPredictionRequest())

        assertTrue(body.contains("date_start"))
        assertTrue(body.contains("organization_role"))
        assertTrue(body.contains("time_start"))
        assertTrue(body.contains(""""title":"Event""""))
        assertTrue(body.contains("participants_total").not())
        assertTrue(body.contains("participants_vo").not())
        assertTrue(body.contains("participants_spo").not())
        assertTrue(body.contains("participants_foreign").not())
        assertTrue(body.contains("participants_other").not())
        assertTrue(body.contains("dateStart").not())
    }

    @Test
    fun `predict serializes organization with camel case isExternal`() = runBlocking {
        var body = ""
        val client = testClient(
            MockEngine { request ->
                body = (request.body as OutgoingContent.ByteArrayContent)
                    .bytes()
                    .decodeToString()
                respond(
                    content = """
                        {"predictedScale":"large_51_200","description":"large","participantsRange":"51-200","probabilities":{"large_51_200":1.0},"confidence":1.0,"similarEvents":[],"modelVersion":"v1","modelTrainedAt":null,"metrics":{},"warnings":[]}
                    """.trimIndent(),
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        client.predict(
            testPredictionRequest().copy(
                organizations = listOf(
                    com.inrotate.analytics.ai.dto.AiOrganizationDto(
                        id = 67,
                        name = "Organization",
                        type = "OTHER",
                        isExternal = true,
                    ),
                ),
            ),
        )

        assertTrue(body.contains("isExternal"))
        assertTrue(body.contains("is_external").not())
    }

    @Test
    fun `train maps 500 response to training failed exception with python message`() = runBlocking {
        val client = testClient(
            MockEngine {
                respond(
                    content = """{"error":"TRAINING_FAILED","message":"Dataset is invalid.","details":{}}""",
                    status = HttpStatusCode.InternalServerError,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                )
            },
        )

        val exception = assertFailsWith<AiTrainingFailedException> {
            client.train("csv".toByteArray())
        }
        assertEquals("Dataset is invalid.", exception.message)
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

    private fun testPredictionRequest(): AiEventScalePredictionRequest = AiEventScalePredictionRequest(
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
