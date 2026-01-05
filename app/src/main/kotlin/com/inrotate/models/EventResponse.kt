package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class EventResponse(
    val id: Int,
    val title: String,
    val description: String?,
    val createdAt: String,
    val dateStart: String,
    val dateEnd: String?,
    val level: EventLevel?,
    val location: String?,
    val timeStart: String?,
    val timeEnd: String?,
    val participantsTotal: Int,
    val participantsOther: Int,
    val participantsSpo: Int,
    val participantsVo: Int,
    val participantsForeign: Int,
    val format: EventFormat,
    val organizationRole: OrganizationRole,
    val types: List<EventType> = emptyList(),
    val organizations: List<OrganizationResponse> = emptyList()
)

fun Event.toResponse() = EventResponse(
    id = this.id,
    title = this.title,
    description = this.description,
    createdAt = this.createdAt.toString(),
    dateStart = this.dateStart,
    dateEnd = this.dateEnd,
    level = this.level,
    location = this.location,
    timeStart = this.timeStart,
    timeEnd = this.timeEnd,
    participantsTotal = this.participantsTotal,
    participantsOther = this.participantsOther,
    participantsSpo = this.participantsSpo,
    participantsVo = this.participantsVo,
    participantsForeign = this.participantsForeign,
    format = this.format,
    organizationRole = this.organizationRole,
    types = this.types,
    organizations = this.organizations.map { it.toResponse() }
)