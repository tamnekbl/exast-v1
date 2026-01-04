package com.inrotate.repository

import com.inrotate.db.EventTypeDAO
import com.inrotate.db.suspendTransaction
import com.inrotate.models.EventType

class EventTypeRepositoryImpl : EventTypeRepository {
    override suspend fun getAll(): List<EventType> = suspendTransaction {
        EventTypeDAO.all().map { it.toEventType() }
    }

    override suspend fun getById(id: Int): EventType? = suspendTransaction {
        EventTypeDAO.findById(id)?.toEventType()
    }

    override suspend fun add(entity: EventType): EventType = throw NotImplementedError()

    override suspend fun update(entity: EventType): EventType = throw NotImplementedError()

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        EventTypeDAO.findById(id)?.delete() != null
    }
}