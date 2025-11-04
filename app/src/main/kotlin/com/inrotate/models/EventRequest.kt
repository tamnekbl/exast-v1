package com.inrotate.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EventRequest(
    val title: String,
    val description: String?,
    val startedAt: String,
    val endedAt: String?,
    val level: EventLevel,
    val location: String?,
    val participantsTotal: Int,
    val participantsOther: Int,
    val participantsSpo: Int,
    val participantsVo: Int,
    val participantsForeign: Int,
    val format: EventFormat,
    val organizationRole: OrganizationRole,
    val types: List<EventType> = emptyList(),
    val organizations: List<Int> = emptyList()
) {
    fun toEvent(id: Int = 0) = Event(
        id = id,
        title = this.title,
        description = this.description,
        createdAt = LocalDateTime.now(),
        startedAt = LocalDateTime.parse(this.startedAt),
        endedAt = this.endedAt?.let { LocalDateTime.parse(it) },
        level = this.level,
        location = this.location,
        participantsTotal = this.participantsTotal,
        participantsOther = this.participantsOther,
        participantsSpo = this.participantsSpo,
        participantsVo = this.participantsVo,
        participantsForeign = this.participantsForeign,
        format = this.format,
        organizationRole = this.organizationRole,
        types = this.types,
        organizations = this.organizations.map { Organization(it) }
    )
}
