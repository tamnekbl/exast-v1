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
    val title: String,
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
    val name: String,
    val type: String?,
    @SerialName("isExternal")
    val isExternal: Boolean? = null,
)

@Serializable
data class AiEventScalePredictionResponse(
    @JsonNames("predicted_scale")
    val predictedScale: String,
    val description: String,
    @JsonNames("participants_range")
    val participantsRange: String,
    val probabilities: Map<String, Double>,
    val confidence: Double,
    @JsonNames("similar_events")
    val similarEvents: List<SimilarEventResponse> = emptyList(),
    @JsonNames("model_version")
    val modelVersion: String,
    @JsonNames("model_trained_at")
    val modelTrainedAt: String? = null,
    val metrics: Map<String, JsonElement>? = null,
    val warnings: List<String> = emptyList(),
)

@Serializable
data class SimilarEventResponse(
    val title: String? = null,
    val description: String? = null,
    @JsonNames("date_start")
    val dateStart: String? = null,
    @JsonNames("date_end")
    val dateEnd: String? = null,
    val level: String? = null,
    val format: String? = null,
    @JsonNames("organization_role")
    val organizationRole: String? = null,
    @JsonNames("main_type")
    val mainType: String? = null,
    @JsonNames("main_organization_type")
    val mainOrganizationType: String? = null,
    @JsonNames("participants_total")
    val participantsTotal: Int? = null,
    @JsonNames("event_scale")
    val eventScale: String,
    val similarity: Double,
)
