package com.inrotate.repository

import com.inrotate.models.Event


interface EventRepository {
    suspend fun allEvents(): List<Event>
    suspend fun eventsByTimeRange(start: String, end: String): List<Event>
    suspend fun eventByName(name: String): Event?
    suspend fun eventById(id: Long): Event?
    suspend fun addEvent(event: Event)
    suspend fun editEvent(id: Long, event: Event)
    suspend fun removeEvent(id: Long)
    suspend fun getFilteredEvents(name: String?, startDate: String?, endDate: String?): List<Event>
}