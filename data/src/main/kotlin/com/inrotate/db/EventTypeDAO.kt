package com.inrotate.db

import com.inrotate.models.EventType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventTypesTable : IntIdTable("event_types") {
    val type = varchar("type", 100)
}

object EventEventTypesTable : Table("event_event_types") {
    val eventId =
        reference("event_id", EventsTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val typeId =
        reference("type_id", EventTypesTable, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(eventId, typeId)
}

class EventTypeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventTypeDAO>(EventTypesTable)

    var type by EventTypesTable.type
    var events by EventDAO via EventEventTypesTable

    fun toEnum(): EventType = EventType.entries.find { it.name == type }
        ?: throw IllegalArgumentException("No enum constant with value: $type")

    fun toEventType() = toEnum()
}

