package com.inrotate.analytics

import com.inrotate.analytics.ai.client.AiServiceClient
import com.inrotate.analytics.ai.dataset.AiTrainingDatasetBuilder
import com.inrotate.analytics.ai.dto.AiModelMetadata
import com.inrotate.analytics.ai.dto.AiPredictionRequest
import com.inrotate.analytics.ai.dto.AiPredictionResponse
import com.inrotate.analytics.ai.dto.AiTrainingResponse
import kotlinx.coroutines.runBlocking
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

        assertEquals(42.0, result.predictedParticipants)
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
        var lastPredictionRequest: AiPredictionRequest? = null

        override suspend fun health(): Boolean = true

        override suspend fun train(csvBytes: ByteArray): AiTrainingResponse {
            trainCalls++
            trainedCsv = csvBytes
            return AiTrainingResponse(
                modelVersion = "model-v1",
                trainedAt = "2026-05-24T10:00:00",
                datasetSize = 1,
                metrics = mapOf("mae" to 1.0),
                status = "trained",
                warnings = emptyList(),
            )
        }

        override suspend fun predict(request: AiPredictionRequest): AiPredictionResponse {
            predictCalls++
            lastPredictionRequest = request
            predictException?.let { throw it }
            return AiPredictionResponse(
                predictedParticipants = 42.0,
                modelVersion = "model-v1",
                modelTrainedAt = "2026-05-24T10:00:00",
                metrics = mapOf("mae" to 1.0),
                warnings = emptyList(),
            )
        }

        override suspend fun getLatestModel(): AiModelMetadata = AiModelMetadata(
            modelVersion = "model-v1",
            trainedAt = "2026-05-24T10:00:00",
            metrics = mapOf("mae" to 1.0),
            baselineMetrics = null,
            warnings = emptyList(),
        )

        override suspend fun getModels(): List<AiModelMetadata> = listOf(getLatestModel())
    }
}
