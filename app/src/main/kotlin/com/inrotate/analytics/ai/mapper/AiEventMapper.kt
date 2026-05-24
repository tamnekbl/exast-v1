package com.inrotate.analytics.ai.mapper

import com.inrotate.analytics.ai.dto.AiEventScalePredictionRequest
import com.inrotate.analytics.ai.dto.AiOrganizationDto
import com.inrotate.analytics.dto.EventDraftRequest
import com.inrotate.models.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object AiEventMapper {
    fun fromEvent(event: Event): AiEventScalePredictionRequest = AiEventScalePredictionRequest(
        title = event.title,
        description = event.description,
        dateStart = event.startedAt.toLocalDate().toString(),
        dateEnd = event.endedAt?.toLocalDate()?.toString(),
        timeStart = event.startedAt.toLocalTime().format(AI_TIME_FORMAT),
        timeEnd = event.endedAt?.toLocalTime()?.format(AI_TIME_FORMAT),
        level = event.level.toAiCode(),
        format = event.format.toAiCode(),
        organizationRole = event.organizationRole.toAiCode(),
        types = event.types.toAiTypeCodes(),
        organizations = event.organizations.map { it.toAiOrganizationDto() },
    )

    fun fromEventRequest(
        request: EventRequest,
        organizations: List<Organization>,
    ): AiEventScalePredictionRequest {
        val startedAt = LocalDateTime.parse(request.startedAt)
        val endedAt = request.endedAt?.let { LocalDateTime.parse(it) }
        val organizationsById = organizations.associateBy { it.id }

        return AiEventScalePredictionRequest(
            title = request.title,
            description = request.description,
            dateStart = startedAt.toLocalDate().toString(),
            dateEnd = endedAt?.toLocalDate()?.toString(),
            timeStart = startedAt.toLocalTime().format(AI_TIME_FORMAT),
            timeEnd = endedAt?.toLocalTime()?.format(AI_TIME_FORMAT),
            level = request.level.toAiCode(),
            format = request.format.toAiCode(),
            organizationRole = request.organizationRole.toAiCode(),
            types = request.types.toAiTypeCodes(),
            organizations = request.organizations.mapNotNull { id ->
                organizationsById[id]?.toAiOrganizationDto()
            },
        )
    }

    fun fromDraftRequest(
        request: EventDraftRequest,
        organizations: List<Organization>,
    ): AiEventScalePredictionRequest {
        val organizationsById = organizations.associateBy { it.id }

        return AiEventScalePredictionRequest(
            title = request.title,
            description = request.description,
            dateStart = request.dateStart,
            dateEnd = request.dateEnd,
            timeStart = request.timeStart.toAiTimeOrNull(),
            timeEnd = request.timeEnd.toAiTimeOrNull(),
            level = request.level.toAiCode(),
            format = request.format.toAiCode(),
            organizationRole = request.organizationRole.toAiCode(),
            types = request.types.toAiTypeCodes(),
            organizations = request.organizations.mapNotNull { id ->
                organizationsById[id]?.toAiOrganizationDto()
            },
        )
    }

    private fun List<EventType>.toAiTypeCodes(): List<String> = map { it.name }

    private fun EventLevel.toAiCode(): String = when (this) {
        EventLevel.structural -> "structural"
        EventLevel.university -> "university"
        EventLevel.municipal -> "municipal"
        EventLevel.regional -> "regional"
        EventLevel.interregional -> "interregional"
        EventLevel.district -> "district"
        EventLevel.national -> "national"
        EventLevel.international -> "international"
        EventLevel.undefined -> throw IllegalArgumentException("Event level must be specified for AI prediction")
    }

    private fun EventFormat.toAiCode(): String = when (this) {
        EventFormat.online -> "online"
        EventFormat.offline -> "offline"
        EventFormat.hybrid -> "hybrid"
    }

    private fun OrganizationRole.toAiCode(): String = when (this) {
        OrganizationRole.participation -> "participation"
        OrganizationRole.organization -> "organization"
        OrganizationRole.assistance -> "assistance"
    }

    private fun Organization.toAiOrganizationDto(): AiOrganizationDto = AiOrganizationDto(
        id = id.toLong(),
        name = name,
        type = type?.type,
        isExternal = isExternal,
    )

    private val AI_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private fun String?.toAiTimeOrNull(): String? = this
        ?.let { LocalTime.parse(it).format(AI_TIME_FORMAT) }
}
