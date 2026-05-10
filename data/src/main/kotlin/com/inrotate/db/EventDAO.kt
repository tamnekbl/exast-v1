package com.inrotate.db

import com.inrotate.models.Event
import com.inrotate.models.EventFormat
import com.inrotate.models.EventLevel
import com.inrotate.models.OrganizationRole
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object EventsTable : IntIdTable("events") {
    val title = varchar("title", 1024)
    val description = text("description").nullable()
    val createdAt = datetime("created_at")
    val startedAt = datetime("started_at")
    val endedAt = datetime("ended_at").nullable()
    val location = varchar("location", 1024).nullable()
    val participantsTotal = integer("participants_total").default(0)
    val participantsOther = integer("participants_other").default(0)
    val participantsSpo = integer("participants_spo").default(0)
    val participantsVo = integer("participants_vo").default(0)
    val participantsForeign = integer("participants_foreign").default(0)
    val format = pgEnum<EventFormat>("format", "event_format").default(EventFormat.offline)
    val level = pgEnum<EventLevel>("level", "event_level").default(EventLevel.undefined)
    val organizationRole = pgEnum<OrganizationRole>("participation_type", "participation_type")
        .default(OrganizationRole.participation)
}

class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventsTable)

    var title by EventsTable.title
    var description by EventsTable.description
    var createdAt by EventsTable.createdAt
    var startedAt by EventsTable.startedAt
    var endedAt by EventsTable.endedAt
    var location by EventsTable.location
    var participantsTotal by EventsTable.participantsTotal
    var participantsOther by EventsTable.participantsOther
    var participantsSpo by EventsTable.participantsSpo
    var participantsVo by EventsTable.participantsVo
    var participantsForeign by EventsTable.participantsForeign
    var format by EventsTable.format
    var level by EventsTable.level
    var organizationRole by EventsTable.organizationRole

    var types by EventTypeDAO via EventEventTypesTable
    var organizations by OrganizationDAO via OrganizationsEventsTable
    val participants by ParticipationDAO referrersOn ParticipationTable.eventId

    fun toEvent() = Event(
        id = id.value,
        title = title,
        description = description,
        createdAt = createdAt,
        startedAt = startedAt,
        endedAt = endedAt,
        level = level,
        location = location,
        participantsTotal = participantsTotal,
        participantsOther = participantsOther,
        participantsSpo = participantsSpo,
        participantsVo = participantsVo,
        participantsForeign = participantsForeign,
        format = format,
        organizationRole = organizationRole,
        participants = participants.map { it.toEventParticipant() },
        types = types.map { it.toEnum() },
        organizations = organizations.map { it.toOrganization() }
    )
}