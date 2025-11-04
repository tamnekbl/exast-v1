package com.inrotate.repository

import com.inrotate.models.*


interface CrudRepository<ID, T> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun add(entity: T): T
    suspend fun update(entity: T): T?
    suspend fun delete(id: ID): Boolean
}

interface EventRepository : CrudRepository<Int, Event> {
    suspend fun getFiltered(title: String?, startDate: String?, endDate: String?): List<Event>
}

interface EventEventTypeRepository {
    suspend fun addTypeToEvent(eventId: Int, typeId: Short): Boolean
    suspend fun removeTypeFromEvent(eventId: Int, typeId: Short): Boolean
    suspend fun getTypes(eventId: Int): List<EventType>
}

interface EventLevelRepository : CrudRepository<Int, EventLevel>

interface EventTypeRepository : CrudRepository<Int, EventType>

interface EventParticipantRepository {
    suspend fun addParticipant(eventId: Int, participantId: Int, roleId: Short): Boolean
    suspend fun removeParticipant(eventId: Int, participantId: Int): Boolean
    suspend fun getParticipants(eventId: Int): List<EventParticipant>
}

interface RoleRepository : CrudRepository<Int, Role>

interface ParticipantRepository : CrudRepository<Int, Participant> {
    suspend fun getByLastName(lastName: String): List<Participant>
    suspend fun getByCourse(course: Int): List<Participant>
}

interface SpecialtyRepository : CrudRepository<Int, Specialty>

interface OrganizationRepository : CrudRepository<Int, Organization> {
    suspend fun getByType(typeId: Int): List<Organization>
}

interface OrganizationTypeRepository : CrudRepository<Int, OrganizationType>


