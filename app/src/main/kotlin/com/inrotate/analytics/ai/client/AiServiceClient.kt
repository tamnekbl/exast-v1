package com.inrotate.analytics.ai.client

import com.inrotate.analytics.ai.dto.AiModelMetadata
import com.inrotate.analytics.ai.dto.AiPredictionRequest
import com.inrotate.analytics.ai.dto.AiPredictionResponse
import com.inrotate.analytics.ai.dto.AiTrainingResponse

interface AiServiceClient {
    suspend fun health(): Boolean
    suspend fun train(csvBytes: ByteArray): AiTrainingResponse
    suspend fun predict(request: AiPredictionRequest): AiPredictionResponse
    suspend fun getLatestModel(): AiModelMetadata
    suspend fun getModels(): List<AiModelMetadata>
}
