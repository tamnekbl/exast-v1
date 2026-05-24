package com.inrotate.analytics.ai.client

import com.inrotate.analytics.ai.dto.*

interface AiServiceClient {
    suspend fun health(): AiHealthResponse
    suspend fun train(csvBytes: ByteArray): AiTrainingResponse
    suspend fun predict(request: AiEventScalePredictionRequest): AiEventScalePredictionResponse
    suspend fun getLatestModel(): AiModelMetadata
    suspend fun getModels(): List<AiModelMetadata>
}
