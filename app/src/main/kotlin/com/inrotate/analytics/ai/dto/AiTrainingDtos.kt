package com.inrotate.analytics.ai.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiTrainingResponse(
    @SerialName("model_version")
    val modelVersion: String,
    @SerialName("trained_at")
    val trainedAt: String,
    @SerialName("dataset_size")
    val datasetSize: Int,
    val metrics: Map<String, Double>,
    val status: String,
    val warnings: List<String>? = null,
)
