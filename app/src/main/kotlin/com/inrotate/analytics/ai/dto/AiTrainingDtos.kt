@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.inrotate.analytics.ai.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

@Serializable
data class AiTrainingResponse(
    @JsonNames("model_version")
    val modelVersion: String,
    @JsonNames("trained_at")
    val trainedAt: String,
    val metrics: Map<String, JsonElement> = emptyMap(),
    @JsonNames("baseline_metrics")
    val baselineMetrics: Map<String, JsonElement> = emptyMap(),
    val warnings: List<String> = emptyList(),
    @JsonNames("feature_schema")
    val featureSchema: List<String> = emptyList(),
    @JsonNames("class_labels")
    val classLabels: List<String> = emptyList(),
    @JsonNames("class_descriptions")
    val classDescriptions: Map<String, String> = emptyMap(),
)
