package com.inrotate.analytics

import com.inrotate.models.*
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository
import java.time.LocalDateTime

fun testOrganization(
    id: Int = 67,
    isExternal: Boolean = true,
): Organization = Organization(
    id = id,
    name = "Test Organization",
    type = OrganizationType(1, "OTHER"),
    isExternal = isExternal,
)

fun testEvent(
    id: Int = 1,
    participantsTotal: Int = 100,
    types: List<EventType> = listOf(EventType.cultural_creative),
    organizations: List<Organization> = listOf(testOrganization()),
): Event = Event(
    id = id,
    title = "Existing event",
    description = "",
    createdAt = LocalDateTime.parse("2026-05-01T09:00:00"),
    startedAt = LocalDateTime.parse("2026-06-01T10:15:00"),
    endedAt = LocalDateTime.parse("2026-06-01T12:30:00"),
    level = EventLevel.university,
    location = "Main hall",
    participantsTotal = participantsTotal,
    participantsOther = 5,
    participantsSpo = 10,
    participantsVo = 85,
    participantsForeign = 0,
    format = EventFormat.offline,
    organizationRole = OrganizationRole.organization,
    types = types,
    organizations = organizations,
)

class FakeEventRepository(
    private val events: List<Event> = emptyList(),
) : EventRepository {
    override suspend fun getAll(): List<Event> = events
    override suspend fun getById(id: Int): Event? = events.find { it.id == id }
    override suspend fun add(entity: Event): Event = entity
    override suspend fun update(entity: Event): Event = entity
    override suspend fun delete(id: Int): Boolean = true
    override suspend fun getFiltered(title: String?, startDate: String?, endDate: String?): List<Event> = events
    override suspend fun addAll(events: List<Event>): List<Event> = events
}

class FakeOrganizationRepository(
    private val organizations: List<Organization> = emptyList(),
) : OrganizationRepository {
    override suspend fun getAll(): List<Organization> = organizations
    override suspend fun getById(id: Int): Organization? = organizations.find { it.id == id }
    override suspend fun add(entity: Organization): Organization = entity
    override suspend fun update(entity: Organization): Organization = entity
    override suspend fun delete(id: Int): Boolean = true
    override suspend fun getByType(typeId: Int): List<Organization> =
        organizations.filter { it.type?.id == typeId }
}
