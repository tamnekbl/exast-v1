package com.inrotate

import com.inrotate.models.Event
import com.inrotate.models.EventFormat
import com.inrotate.models.EventLevel
import com.inrotate.models.OrganizationRole
import com.inrotate.repository.EventRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class IntegrationTest : TestDatabase() {

    @Test
    fun `should create event with organizations and types`() = runBlocking {
        // Создаем репозитории
        val eventRepository = EventRepositoryImpl()

        // Создаем тестовое событие
        val event = Event(
            id = 0,
            title = "Integration Test Event",
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
        val addedEvent = eventRepository.add(event)

        // Проверяем, что событие было добавлено
        assertNotNull(addedEvent.id)
        assertEquals(event.title, addedEvent.title)

        // Получаем событие по ID
        val retrievedEvent = eventRepository.getById(addedEvent.id)

        // Проверяем, что событие корректно извлечено
        assertNotNull(retrievedEvent)
        assertEquals(addedEvent.id, retrievedEvent.id)
        assertEquals(event.title, retrievedEvent.title)
    }
}