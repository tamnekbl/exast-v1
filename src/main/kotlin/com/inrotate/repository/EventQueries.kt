package com.inrotate.repository


import com.inrotate.db.EventDAO
import com.inrotate.db.EventsTable
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Event
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventQueries : EventRepository {
    override suspend fun allEvents(): List<Event> = suspendTransaction {
        EventDAO.all().map { it.toEvent() }
    }

    override suspend fun eventsByTimeRange(start: String, end: String): List<Event> = suspendTransaction {
        val startDateTime = LocalDateTime.parse(start)
        val endDateTime = LocalDateTime.parse(end)

        EventDAO
            .find(
                (EventsTable.startedAt greaterEq startDateTime) and
                        (EventsTable.startedAt lessEq endDateTime)
            )
            .map { it.toEvent() }
    }

    override suspend fun eventByName(name: String): Event? = suspendTransaction {
        EventDAO
            .find { (EventsTable.name eq name) }
            .limit(1)
            .map { it.toEvent() }
            .firstOrNull()
    }

    override suspend fun getFilteredEvents(
        name: String?,
        startDate: String?,
        endDate: String?
    ): List<Event> = suspendTransaction {
        val filters = buildList {
            when {
                !name.isNullOrBlank() -> add(EventsTable.name like "%$name%")
                !startDate.isNullOrBlank() && !endDate.isNullOrBlank() ->{
                    val startDateTime = LocalDateTime.parse(startDate)
                    val endDateTime = LocalDateTime.parse(endDate)
                    add((EventsTable.startedAt greaterEq startDateTime) and (EventsTable.endedAt lessEq endDateTime))
                }
                !startDate.isNullOrBlank() -> {
                    val startDateTime = LocalDateTime.parse(startDate)
                    add(EventsTable.startedAt greaterEq startDateTime)
                }
                !endDate.isNullOrBlank() -> {
                    val endDateTime = LocalDateTime.parse(endDate)
                    add(EventsTable.endedAt lessEq endDateTime)
                }
            }
        }

        EventDAO
            .find(filters.reduceOrNull { acc, filter -> acc and filter } ?: Op.TRUE)
            .toList()
            .map { it.toEvent() }
    }

    override suspend fun eventById(id: Long): Event? = suspendTransaction {
        EventDAO.findById(id)?.toEvent()
    }

    override suspend fun addEvent(event: Event): Unit = suspendTransaction {
        //todo проверка на валидность времён. время начала не позже времени конца
        EventDAO.new {
            name = event.name
            description = event.description
            createdAt = LocalDateTime.parse(event.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            startedAt = LocalDateTime.parse(event.startedAt, DateTimeFormatter.ISO_DATE_TIME)
            endedAt = LocalDateTime.parse(event.endedAt, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    override suspend fun editEvent(id: Long, event: Event):Unit = suspendTransaction{
        EventDAO.findByIdAndUpdate(id){
            it.name = event.name
            it.description = event.description
            it.createdAt = LocalDateTime.parse(event.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            it.startedAt = LocalDateTime.parse(event.startedAt, DateTimeFormatter.ISO_DATE_TIME)
            it.endedAt = LocalDateTime.parse(event.endedAt, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    override suspend fun removeEvent(id: Long):Unit = suspendTransaction{
        EventsTable.deleteWhere { EventsTable.id.eq(id) }
    }
}