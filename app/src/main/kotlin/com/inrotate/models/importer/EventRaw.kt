package com.inrotate.models.importer

import com.inrotate.models.*
import java.time.LocalDateTime

data class EventRaw(
    val title: String,
    val date: String,
    val level: String,
    val types: String,
    val location: String,
    val format: String,
    val participantsTotal: Int,
    val participantsOther: Int,
    val participantsSpo: Int,
    val participantsVo: Int,
    val participantsForeign: Int,
    val organizationRole: String,
    val organizations: String,
    val description: String,
) {
    fun toEvent(): Event {
        val dateTimeRange = UniversalDateParser.parse(this.date)
            ?: throw IllegalArgumentException("there is no such date format \"${this.date}\"")
        val levelEnum = EventLevel.entries.find { it.value == this.level.lowercase() }
            ?: throw IllegalArgumentException("level \"${this.level}\" not found")
        val formatEnum = EventFormat.entries.find { it.value == this.format.lowercase() }
            ?: throw IllegalArgumentException("format \"${this.format}\" not found")
        val organizationRoleEnum = OrganizationRole.entries.find { it.value == this.organizationRole.lowercase() }
            ?: throw IllegalArgumentException("organization role \"${this.organizationRole}\" not found")
        val eventTypes = EventType.parseRaw(this.types)
            .ifEmpty { throw IllegalArgumentException("no event types for event") }
        val organizationsParsed = XlsxParser.parseOrganizations(this.organizations) //internal throw
            .ifEmpty { throw IllegalArgumentException("no organizations for event") }
        return Event(
            title = this.title,
            description = this.description,
            createdAt = LocalDateTime.now(),
            startedAt = dateTimeRange.start,
            endedAt = dateTimeRange.end,
            level = levelEnum,
            location = this.location,
            participantsTotal = this.participantsTotal,
            participantsOther = this.participantsOther,
            participantsSpo = this.participantsSpo,
            participantsVo = this.participantsVo,
            participantsForeign = this.participantsForeign,
            format = formatEnum,
            organizationRole = organizationRoleEnum,
            participants = listOf(),
            types = eventTypes,
            organizations = organizationsParsed,
        )
    }
}
