package com.inrotate.repository


import com.inrotate.db.events.EventDAO
import com.inrotate.db.events.EventsTable
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Event
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import java.time.LocalDateTime

class EventRepositoryImpl : EventRepository {
    override suspend fun getAll(): List<Event> = suspendTransaction {
        EventDAO.all().map { it.toEvent() }
    }

    override suspend fun getFiltered(
        title: String?,
        startDate: String?,
        endDate: String?
    ): List<Event> = suspendTransaction {
        val filters = buildList {
            when {
                !title.isNullOrBlank() -> add(EventsTable.title like "%$title%")
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

    override suspend fun getById(id: Int): Event? = suspendTransaction {
        EventDAO.findById(id)?.toEvent()
    }

    override suspend fun add(entity: Event): Event = suspendTransaction {
        //todo проверка на валидность времён. время начала не позже времени конца
        EventDAO.new {
            title = entity.title
            description = entity.description
            createdAt = entity.createdAt
            startedAt = entity.startedAt
            endedAt = entity.endedAt
            location = entity.location
            participantsTotal = entity.participantsTotal
            participantsOther = entity.participantsOther
            participantsSpo = entity.participantsSpo
            participantsVo = entity.participantsVo
            participantsForeign = entity.participantsForeign
            format = entity.format
            level = entity.level
            organizationRole = entity.organizationRole
        }.toEvent()
    }

    override suspend fun update(entity: Event): Event? = suspendTransaction {
        EventDAO.findByIdAndUpdate(entity.id) {
            it.title = entity.title
            it.description = entity.description
            it.createdAt = entity.createdAt
            it.startedAt = entity.startedAt
            it.endedAt = entity.endedAt
            it.location = entity.location
            it.participantsTotal = entity.participantsTotal
            it.participantsOther = entity.participantsOther
            it.participantsSpo = entity.participantsSpo
            it.participantsVo = entity.participantsVo
            it.participantsForeign = entity.participantsForeign
            it.format = entity.format
            it.level = entity.level
            it.organizationRole = entity.organizationRole
        }?.toEvent()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        EventDAO.findById(id)?.delete() != null
    }
}