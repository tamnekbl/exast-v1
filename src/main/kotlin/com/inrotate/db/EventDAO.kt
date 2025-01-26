package com.inrotate.db

import com.inrotate.models.Event
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object EventsTable : LongIdTable("events") {
    val name = varchar("name", 255)
    val description = text("description")
    val createdAt = datetime("created_at")
    val startedAt = datetime("started_at")
    val endedAt = datetime("ended_at")
}

class EventDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EventDAO>(EventsTable)

    var name by EventsTable.name
    var description by EventsTable.description
    var createdAt by EventsTable.createdAt
    var startedAt by EventsTable.startedAt
    var endedAt by EventsTable.endedAt

    fun toEvent() = Event(
        id.value,
        name,
        description,
        createdAt.toString(),
        startedAt.toString(),
        endedAt.toString()
    )
}







object StructureTable : Table("structures") {
    val id = varchar("id", 50)
    val name = varchar("name", 255)
    val description = text("description")
    override val primaryKey = PrimaryKey(id, name = "PK_Structures_Id")
}

object ParticipantTable : Table("participants") {
    val id = varchar("id", 50)
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
}