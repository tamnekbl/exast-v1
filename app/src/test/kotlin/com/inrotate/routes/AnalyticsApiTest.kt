package com.inrotate.routes

import com.inrotate.analytics.*
import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.*
import com.inrotate.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnalyticsApiTest {
    @Test
    fun `get feature insights success returns dashboard blocks`() = testApplication {
        val aiClient = FakeAiServiceClient()
        configureAnalyticsTestRoute(aiClient)

        val response = client.get("/api/v1/analytics/ai/feature-insights")

        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains(""""model""""))
        assertTrue(body.contains(""""dataset""""))
        assertTrue(body.contains(""""featureImportance""""))
        assertTrue(body.contains(""""factors""""))
        assertTrue(body.contains(""""charts""""))
    }

    @Test
    fun `get feature insights maps model not found to not found response`() = testApplication {
        configureAnalyticsTestRoute(
            FakeAiServiceClient(featureInsightsException = AiModelNotFoundException()),
        )

        val response = client.get("/api/v1/analytics/ai/feature-insights")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("message"))
    }

    @Test
    fun `get feature insights maps topN query parameter`() = testApplication {
        val aiClient = FakeAiServiceClient()
        configureAnalyticsTestRoute(aiClient)

        client.get("/api/v1/analytics/ai/feature-insights?topN=15")

        assertEquals(15, aiClient.lastFeatureInsightsTopN)
    }

    @Test
    fun `get feature insights maps modelVersion query parameter`() = testApplication {
        val aiClient = FakeAiServiceClient()
        configureAnalyticsTestRoute(aiClient)

        client.get("/api/v1/analytics/ai/feature-insights?modelVersion=event_scale_v2")

        assertEquals("event_scale_v2", aiClient.lastFeatureInsightsModelVersion)
    }

    @Test
    fun `get feature insights rejects invalid topN`() = testApplication {
        val aiClient = FakeAiServiceClient()
        configureAnalyticsTestRoute(aiClient)

        val tooSmall = client.get("/api/v1/analytics/ai/feature-insights?topN=4")
        val tooLarge = client.get("/api/v1/analytics/ai/feature-insights?topN=51")

        assertEquals(HttpStatusCode.BadRequest, tooSmall.status)
        assertEquals(HttpStatusCode.BadRequest, tooLarge.status)
        assertEquals(0, aiClient.featureInsightsCalls)
    }

    private fun ApplicationTestBuilder.configureAnalyticsTestRoute(aiClient: FakeAiServiceClient) {
        application {
            configureSerialization()
            val eventRepository = FakeEventRepository(listOf(testEvent()))
            val service = AiAnalyticsService(
                config = AiServiceConfig(
                    baseUrl = "http://ai.local",
                    connectTimeoutMillis = 5_000,
                    requestTimeoutMillis = 30_000,
                    socketTimeoutMillis = 30_000,
                    enabled = true,
                ),
                eventRepository = eventRepository,
                organizationRepository = FakeOrganizationRepository(listOf(testOrganization())),
                datasetBuilder = AiTrainingDatasetBuilder(eventRepository),
                aiServiceClient = aiClient,
            )
            routing {
                route("/api/v1") {
                    configureAnalytics(service, AiTrainingDatasetBuilder(eventRepository))
                }
            }
        }
    }

    private class FakeAiServiceClient(
        private val featureInsightsException: AnalyticsException? = null,
    ) : AiServiceClient {
        var featureInsightsCalls = 0
        var lastFeatureInsightsModelVersion: String? = null
        var lastFeatureInsightsTopN: Int? = null

        override suspend fun health(): AiHealthResponse = AiHealthResponse("ok")

        override suspend fun train(csvBytes: ByteArray): AiTrainingResponse = AiTrainingResponse(
            modelVersion = "model-v1",
            trainedAt = "2026-06-01T10:00:00",
        )

        override suspend fun predict(request: AiEventScalePredictionRequest): AiEventScalePredictionResponse =
            AiEventScalePredictionResponse(
                predictedScale = "mass_201_plus",
                description = "mass",
                participantsRange = "201+",
                probabilities = mapOf("mass_201_plus" to 1.0),
                confidence = 1.0,
                modelVersion = "model-v1",
                metrics = mapOf("accuracy" to JsonPrimitive(1.0)),
            )

        override suspend fun getLatestModel(): AiModelMetadata = AiModelMetadata(
            modelVersion = "model-v1",
            trainedAt = "2026-06-01T10:00:00",
        )

        override suspend fun getModels(): List<AiModelMetadata> = listOf(getLatestModel())

        override suspend fun getFeatureInsights(
            modelVersion: String?,
            topN: Int,
        ): AiFeatureInsightsResponse {
            featureInsightsCalls++
            lastFeatureInsightsModelVersion = modelVersion
            lastFeatureInsightsTopN = topN
            featureInsightsException?.let { throw it }
            return testFeatureInsightsResponse(modelVersion = modelVersion ?: "event_scale_v1")
        }
    }
}
