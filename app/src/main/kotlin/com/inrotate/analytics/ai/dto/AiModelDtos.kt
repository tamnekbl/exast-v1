@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.inrotate.analytics.ai.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class AiModelMetadata(
    @JsonNames("model_version")
    val modelVersion: String,
    @JsonNames("trained_at")
    val trainedAt: String? = null,
    val metrics: Map<String, JsonElement> = emptyMap(),
    @JsonNames("baseline_metrics")
    val baselineMetrics: Map<String, JsonElement> = emptyMap(),
    val warnings: List<String> = emptyList(),
)

@Serializable
data class AiHealthResponse(
    val status: String,
)

@Serializable
data class AiErrorResponse(
    val error: String,
    val message: String,
    val details: Map<String, JsonElement> = emptyMap(),
)
