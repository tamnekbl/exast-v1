package com.inrotate.analytics

import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AiAnalyticsServiceTest {
    @Test
    fun `trainAttendanceModel builds dataset and calls ai client train`() = runBlocking {
        val aiClient = FakeAiServiceClient()
        val service = service(aiClient = aiClient)

        val result = service.trainAttendanceModel()

        assertEquals("model-v1", result.modelVersion)
        assertNotNull(aiClient.trainedCsv)
        assertEquals(1, aiClient.trainCalls)
    }

    @Test
    fun `predictAttendanceForEvent loads event and calls ai client predict`() = runBlocking {
        val aiClient = FakeAiServiceClient()
        val service = service(aiClient = aiClient)

        val result = service.predictAttendanceForEvent(1)

        assertEquals("large_51_200", result.predictedScale)
        assertEquals("large", result.scaleDescription)
        assertEquals(0.51, result.confidence)
        assertEquals("Historical event", result.similarEvents.single().title)
        assertEquals(1, aiClient.predictCalls)
        assertEquals("Existing event", aiClient.lastPredictionRequest?.title)
        assertEquals("cultural_creative", aiClient.lastPredictionRequest?.types?.single())
    }

    @Test
    fun `disabled ai service throws disabled exception`() = runBlocking {
        val service = service(
            config = config(enabled = false),
        )

        assertFailsWith<AiServiceDisabledException> {
            service.trainAttendanceModel()
        }
    }

    @Test
    fun `unavailable ai service propagates unavailable exception`() = runBlocking {
        val service = service(
            aiClient = FakeAiServiceClient(
                predictException = AiServiceUnavailableException(),
            ),
        )

        assertFailsWith<AiServiceUnavailableException> {
            service.predictAttendanceForEvent(1)
        }
    }

    @Test
    fun `timeout from ai service propagates timeout exception`() = runBlocking {
        val service = service(
            aiClient = FakeAiServiceClient(
                predictException = AiServiceTimeoutException(),
            ),
        )

        assertFailsWith<AiServiceTimeoutException> {
            service.predictAttendanceForEvent(1)
        }
    }

    private fun service(
        config: AiServiceConfig = config(),
        aiClient: FakeAiServiceClient = FakeAiServiceClient(),
    ): AiAnalyticsService {
        val eventRepository = FakeEventRepository(listOf(testEvent()))
        return AiAnalyticsService(
            config = config,
            eventRepository = eventRepository,
            organizationRepository = FakeOrganizationRepository(listOf(testOrganization())),
            datasetBuilder = AiTrainingDatasetBuilder(eventRepository),
            aiServiceClient = aiClient,
        )
    }

    private fun config(enabled: Boolean = true): AiServiceConfig = AiServiceConfig(
        baseUrl = "http://ai.local",
        connectTimeoutMillis = 5_000,
        requestTimeoutMillis = 30_000,
        socketTimeoutMillis = 30_000,
        enabled = enabled,
    )

    private class FakeAiServiceClient(
        private val predictException: AnalyticsException? = null,
    ) : AiServiceClient {
        var trainCalls = 0
        var predictCalls = 0
        var trainedCsv: ByteArray? = null
        var lastPredictionRequest: AiEventScalePredictionRequest? = null

        override suspend fun health(): AiHealthResponse = AiHealthResponse("ok")

        override suspend fun train(csvBytes: ByteArray): AiTrainingResponse {
            trainCalls++
            trainedCsv = csvBytes
            return AiTrainingResponse(
                modelVersion = "model-v1",
                trainedAt = "2026-05-24T10:00:00",
                metrics = mapOf("accuracy" to JsonPrimitive(1.0)),
                baselineMetrics = emptyMap(),
                warnings = emptyList(),
                featureSchema = emptyList(),
                classLabels = listOf("large_51_200"),
                classDescriptions = mapOf("large_51_200" to "large"),
            )
        }

        override suspend fun predict(request: AiEventScalePredictionRequest): AiEventScalePredictionResponse {
            predictCalls++
            lastPredictionRequest = request
            predictException?.let { throw it }
            return AiEventScalePredictionResponse(
                predictedScale = "large_51_200",
                description = "large",
                participantsRange = "51-200",
                probabilities = mapOf("large_51_200" to 0.51),
                confidence = 0.51,
                similarEvents = listOf(
                    SimilarEventResponse(
                        title = "Historical event",
                        description = "Similar",
                        dateStart = "2023-05-02",
                        dateEnd = "2023-05-02",
                        level = "university",
                        format = "offline",
                        organizationRole = "organization",
                        mainType = "cultural_creative",
                        mainOrganizationType = "OTHER",
                        participantsTotal = 2624,
                        eventScale = "mass_201_plus",
                        similarity = 0.86,
                    ),
                ),
                modelVersion = "model-v1",
                modelTrainedAt = "2026-05-24T10:00:00",
                metrics = mapOf("accuracy" to JsonPrimitive(1.0)),
                warnings = emptyList(),
            )
        }

        override suspend fun getLatestModel(): AiModelMetadata = AiModelMetadata(
            modelVersion = "model-v1",
            trainedAt = "2026-05-24T10:00:00",
            metrics = mapOf("accuracy" to JsonPrimitive(1.0)),
            baselineMetrics = emptyMap(),
            warnings = emptyList(),
        )

        override suspend fun getModels(): List<AiModelMetadata> = listOf(getLatestModel())
    }
}
