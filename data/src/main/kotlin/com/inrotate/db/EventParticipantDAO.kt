package com.inrotate.db

import com.inrotate.models.EventParticipant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Таблица связи между событиями и участниками, с ролью
 */
object EventParticipantsTable : IntIdTable("event_participants") {
    val eventId = reference("event_id", EventsTable, onDelete = ReferenceOption.CASCADE)
    val participantId = reference("participant_id", ParticipantsTable, onDelete = ReferenceOption.CASCADE)
    val roleId = reference("role_id", RolesTable, onDelete = ReferenceOption.NO_ACTION)
}

class EventParticipantDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventParticipantDAO>(EventParticipantsTable)

    var event by EventDAO referencedOn EventParticipantsTable.eventId
    var participant by ParticipantDAO referencedOn EventParticipantsTable.participantId
    var role by RoleDAO referencedOn EventParticipantsTable.roleId

    fun toEventParticipant() = EventParticipant(
        participant = participant.toParticipant(),
        role = role.toRole()
    )
}