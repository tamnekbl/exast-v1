package com.inrotate.repository


import com.inrotate.db.events.Event
import com.inrotate.db.events.EventDAO
import com.inrotate.db.events.EventsTable
import com.inrotate.db.suspendTransaction
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
    override suspend fun getAll(): List<Event> = suspendTransaction {
        EventDAO.all().map { it.toEvent() }
    }

    override suspend fun getFiltered(
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

    override suspend fun getById(id: Long): Event? = suspendTransaction {
        EventDAO.findById(id)?.toEvent()
    }

    override suspend fun add(event: Event): Unit = suspendTransaction {
        //todo проверка на валидность времён. время начала не позже времени конца
        EventDAO.new {
            name = event.name
            description = event.description
            createdAt = LocalDateTime.parse(event.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            startedAt = LocalDateTime.parse(event.startedAt, DateTimeFormatter.ISO_DATE_TIME)
            endedAt = LocalDateTime.parse(event.endedAt, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    override suspend fun edit(id: Long, event: Event): Unit = suspendTransaction {
        EventDAO.findByIdAndUpdate(id){
            it.name = event.name
            it.description = event.description
            it.createdAt = LocalDateTime.parse(event.createdAt, DateTimeFormatter.ISO_DATE_TIME)
            it.startedAt = LocalDateTime.parse(event.startedAt, DateTimeFormatter.ISO_DATE_TIME)
            it.endedAt = LocalDateTime.parse(event.endedAt, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    override suspend fun remove(id: Long): Unit = suspendTransaction {
        EventsTable.deleteWhere { EventsTable.id.eq(id) }
    }
}