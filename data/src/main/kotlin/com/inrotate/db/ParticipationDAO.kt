package com.inrotate.db

import com.inrotate.models.EventParticipant
import org.jetbrains.exposed.dao.CompositeEntity
import org.jetbrains.exposed.dao.CompositeEntityClass
import org.jetbrains.exposed.dao.id.CompositeID
import org.jetbrains.exposed.dao.id.CompositeIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * Таблица связи между событиями и участниками, с ролью
 */
object ParticipationTable : CompositeIdTable("participation") {
    val eventId = reference(
        name = "event_id",
        foreign = EventsTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val participantId = reference(
        name = "participant_id",
        foreign = ParticipantsTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val roleId = reference(
        name = "role_id",
        foreign = RolesTable,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    // Определяем составной первичный ключ
    override val primaryKey = PrimaryKey(eventId, participantId)

    // Привязываем ID к колонкам
    init {
        addIdColumn(eventId)
        addIdColumn(participantId)
    }
}

class ParticipationDAO(id: EntityID<CompositeID>) : CompositeEntity(id) {
    companion object : CompositeEntityClass<ParticipationDAO>(ParticipationTable)

    var event by EventDAO referencedOn ParticipationTable.eventId
    var participant by ParticipantDAO referencedOn ParticipationTable.participantId
    var role by RoleDAO referencedOn ParticipationTable.roleId

    fun toEventParticipant() = EventParticipant(
        participant = participant.toParticipant(),
        role = role.toRole()
    )
}