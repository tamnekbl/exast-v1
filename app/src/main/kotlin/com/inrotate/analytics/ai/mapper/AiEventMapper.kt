package com.inrotate.analytics.ai.mapper

import com.inrotate.analytics.ai.dto.AiOrganizationDto
import com.inrotate.analytics.ai.dto.AiPredictionRequest
import com.inrotate.analytics.dto.EventDraftRequest
import com.inrotate.models.Event
import com.inrotate.models.EventRequest
import com.inrotate.models.EventType
import com.inrotate.models.Organization
import java.time.LocalDateTime

object AiEventMapper {
    fun fromEvent(event: Event): AiPredictionRequest = AiPredictionRequest(
        title = event.title,
        description = event.description,
        dateStart = event.startedAt.toLocalDate().toString(),
        dateEnd = event.endedAt?.toLocalDate()?.toString(),
        timeStart = event.startedAt.toLocalTime().toString(),
        timeEnd = event.endedAt?.toLocalTime()?.toString(),
        level = event.level.name,
        format = event.format.name,
        organizationRole = event.organizationRole.name,
        types = event.types.toAiTypeCodes(),
        organizations = event.organizations.map { it.toAiOrganizationDto() },
    )

    fun fromEventRequest(
        request: EventRequest,
        organizations: List<Organization>,
    ): AiPredictionRequest {
        val startedAt = LocalDateTime.parse(request.startedAt)
        val endedAt = request.endedAt?.let { LocalDateTime.parse(it) }
        val organizationsById = organizations.associateBy { it.id }

        return AiPredictionRequest(
            title = request.title,
            description = request.description,
            dateStart = startedAt.toLocalDate().toString(),
            dateEnd = endedAt?.toLocalDate()?.toString(),
            timeStart = startedAt.toLocalTime().toString(),
            timeEnd = endedAt?.toLocalTime()?.toString(),
            level = request.level.name,
            format = request.format.name,
            organizationRole = request.organizationRole.name,
            types = request.types.toAiTypeCodes(),
            organizations = request.organizations.mapNotNull { id ->
                organizationsById[id]?.toAiOrganizationDto()
            },
        )
    }

    fun fromDraftRequest(
        request: EventDraftRequest,
        organizations: List<Organization>,
    ): AiPredictionRequest {
        val startedAt = LocalDateTime.parse(request.startedAt)
        val endedAt = request.endedAt?.let { LocalDateTime.parse(it) }
        val organizationsById = organizations.associateBy { it.id }

        return AiPredictionRequest(
            title = request.title,
            description = request.description,
            dateStart = startedAt.toLocalDate().toString(),
            dateEnd = endedAt?.toLocalDate()?.toString(),
            timeStart = startedAt.toLocalTime().toString(),
            timeEnd = endedAt?.toLocalTime()?.toString(),
            level = request.level.name,
            format = request.format.name,
            organizationRole = request.organizationRole.name,
            types = request.types.toAiTypeCodes(),
            organizations = request.organizations.mapNotNull { id ->
                organizationsById[id]?.toAiOrganizationDto()
            },
        )
    }

    private fun List<EventType>.toAiTypeCodes(): List<String> = map { it.name }

    private fun Organization.toAiOrganizationDto(): AiOrganizationDto = AiOrganizationDto(
        id = id,
        name = name,
        type = type?.type,
        isExternal = isExternal,
    )
}
