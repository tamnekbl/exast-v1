package com.inrotate.repository

import com.inrotate.TestDatabase
import com.inrotate.models.Event
import com.inrotate.models.EventFormat
import com.inrotate.models.EventLevel
import com.inrotate.models.OrganizationRole
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EventRepositoryImplTest : TestDatabase() {
    private lateinit var repository: EventRepository

    override fun setupDatabase() {
        super.setupDatabase()
        repository = EventRepositoryImpl()
    }

    @Test
    fun `should add and retrieve event`() = runBlocking {
        // Создаем тестовое событие
        val event = Event(
            id = 0,
            title = "Test Event",
            description = "Test Description",
            createdAt = LocalDateTime.now(),
            startedAt = LocalDateTime.now().plusDays(1),
            endedAt = LocalDateTime.now().plusDays(2),
            level = EventLevel.international,
            location = "Test Location",
            participantsTotal = 10,
            participantsOther = 2,
            participantsSpo = 3,
            participantsVo = 4,
            participantsForeign = 1,
            format = EventFormat.online,
            organizationRole = OrganizationRole.organization,
            participants = emptyList(),
            types = emptyList(),
            organizations = emptyList()
        )

        // Добавляем событие
        val addedEvent = repository.add(event)

        // Проверяем, что событие было добавлено
        assertNotNull(addedEvent.id)
        assertEquals(event.title, addedEvent.title)
        assertEquals(event.description, addedEvent.description)

        // Получаем событие по ID
        val retrievedEvent = repository.getById(addedEvent.id)

        // Проверяем, что событие корректно извлечено
        assertNotNull(retrievedEvent)
        assertEquals(addedEvent.id, retrievedEvent.id)
        assertEquals(event.title, retrievedEvent.title)
    }

    @Test
    fun `should update event`() = runBlocking {
        // Создаем и добавляем событие
        val originalEvent = Event(
            id = 0,
            title = "Original Event",
            description = "Original Description",
            createdAt = LocalDateTime.now(),
            startedAt = LocalDateTime.now().plusDays(1),
            endedAt = LocalDateTime.now().plusDays(2),
            level = EventLevel.regional,
            location = "Original Location",
            participantsTotal = 5,
            participantsOther = 1,
            participantsSpo = 1,
            participantsVo = 2,
            participantsForeign = 1,
            format = EventFormat.offline,
            organizationRole = OrganizationRole.participation,
            participants = emptyList(),
            types = emptyList(),
            organizations = emptyList()
        )

        val addedEvent = repository.add(originalEvent)

        // Обновляем событие
        val updatedEvent = addedEvent.copy(
            title = "Updated Event",
            description = "Updated Description",
            level = EventLevel.national
        )

        val result = repository.update(updatedEvent)

        // Проверяем, что событие было обновлено
        assertNotNull(result)
        assertEquals("Updated Event", result.title)
        assertEquals("Updated Description", result.description)
        assertEquals(EventLevel.national, result.level)
    }

    @Test
    fun `should delete event`() = runBlocking {
        // Создаем и добавляем событие
        val event = Event(
            id = 0,
            title = "Event to delete",
            description = "Description",
            createdAt = LocalDateTime.now(),
            startedAt = LocalDateTime.now().plusDays(1),
            endedAt = LocalDateTime.now().plusDays(2),
            level = EventLevel.structural,
            location = "Location",
            participantsTotal = 5,
            participantsOther = 0,
            participantsSpo = 2,
            participantsVo = 2,
            participantsForeign = 1,
            format = EventFormat.hybrid,
            organizationRole = OrganizationRole.organization,
            participants = emptyList(),
            types = emptyList(),
            organizations = emptyList()
        )

        val addedEvent = repository.add(event)
        val eventId = addedEvent.id

        // Удаляем событие
        val deleted = repository.delete(eventId)

        // Проверяем, что событие было удалено
        assertTrue(deleted)

        // Пытаемся получить удаленное событие
        val retrievedEvent = repository.getById(eventId)
        assertNull(retrievedEvent)
    }

    @Test
    fun `should get all events`() = runBlocking {
        // Добавляем несколько событий
        val event1 = Event(
            id = 0,
            title = "Event 1",
            description = "Description 1",
            createdAt = LocalDateTime.now(),
            startedAt = LocalDateTime.now().plusDays(1),
            endedAt = LocalDateTime.now().plusDays(2),
            level = EventLevel.regional,
            location = "Location 1",
            participantsTotal = 5,
            participantsOther = 0,
            participantsSpo = 2,
            participantsVo = 2,
            participantsForeign = 1,
            format = EventFormat.offline,
            organizationRole = OrganizationRole.participation,
            participants = emptyList(),
            types = emptyList(),
            organizations = emptyList()
        )

        val event2 = event1.copy(id = 0, title = "Event 2", description = "Description 2")

        repository.add(event1)
        repository.add(event2)

        // Получаем все события
        val events = repository.getAll()

        // Проверяем, что все события были получены
        assertEquals(2, events.size)
        assertTrue(events.any { it.title == "Event 1" })
        assertTrue(events.any { it.title == "Event 2" })
    }
}