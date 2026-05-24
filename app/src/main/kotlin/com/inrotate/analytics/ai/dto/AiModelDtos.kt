package com.inrotate.analytics.ai.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiModelMetadata(
    @SerialName("model_version")
    val modelVersion: String,
    @SerialName("trained_at")
    val trainedAt: String?,
    val metrics: Map<String, Double>?,
    @SerialName("baseline_metrics")
    val baselineMetrics: Map<String, Double>?,
    val warnings: List<String>?,
)
