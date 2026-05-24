@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.inrotate.analytics.ai.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNames

typealias LocalDate = String
typealias LocalTime = String

@Serializable
enum class EventScale {
    @SerialName("small_1_20")
    SMALL_1_20,

    @SerialName("medium_21_50")
    MEDIUM_21_50,

    @SerialName("large_51_200")
    LARGE_51_200,

    @SerialName("mass_201_plus")
    MASS_201_PLUS,
}

@Serializable
data class AiEventScalePredictionRequest(
    val title: String?,
    val description: String?,
    @SerialName("date_start")
    val dateStart: LocalDate,
    @SerialName("date_end")
    val dateEnd: LocalDate?,
    @SerialName("time_start")
    val timeStart: LocalTime?,
    @SerialName("time_end")
    val timeEnd: LocalTime?,
    val level: String,
    val format: String,
    @SerialName("organization_role")
    val organizationRole: String,
    val types: List<String>,
    val organizations: List<AiOrganizationDto>,
)

@Serializable
data class AiOrganizationDto(
    val id: Long?,
    val name: String?,
    val type: String?,
    @SerialName("isExternal")
    val isExternal: Boolean = false,
)

@Serializable
data class AiEventScalePredictionResponse(
    @JsonNames("predicted_scale")
    val predictedScale: EventScale,
    val description: String,
    @JsonNames("participants_range")
    val participantsRange: String,
    val probabilities: Map<String, Double>,
    val confidence: Double,
    @JsonNames("model_version")
    val modelVersion: String? = null,
    @JsonNames("model_trained_at")
    val modelTrainedAt: String? = null,
    val metrics: Map<String, JsonElement> = emptyMap(),
    val warnings: List<String> = emptyList(),
)
