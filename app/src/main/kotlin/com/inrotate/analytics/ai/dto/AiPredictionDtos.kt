package com.inrotate.analytics.ai.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias LocalDate = String
typealias LocalTime = String

@Serializable
data class AiPredictionRequest(
    val title: String,
    val description: String?,
    val dateStart: LocalDate,
    val dateEnd: LocalDate?,
    val timeStart: LocalTime?,
    val timeEnd: LocalTime?,
    val level: String,
    val format: String,
    val organizationRole: String,
    val types: List<String>,
    val organizations: List<AiOrganizationDto>,
)

@Serializable
data class AiOrganizationDto(
    val id: Int?,
    val name: String,
    val type: String?,
    @SerialName("isExternal")
    val isExternal: Boolean?,
)

@Serializable
data class AiPredictionResponse(
    @SerialName("predicted_participants")
    val predictedParticipants: Double,
    @SerialName("model_version")
    val modelVersion: String,
    @SerialName("model_trained_at")
    val modelTrainedAt: String?,
    val metrics: Map<String, Double>?,
    val warnings: List<String> = emptyList(),
)
