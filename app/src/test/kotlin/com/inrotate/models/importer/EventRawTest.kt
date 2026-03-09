package com.inrotate.models.importer

import com.inrotate.models.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class EventRawTest {

    private val validEventRaw = EventRaw(
        title = "Test Event",
        date = "01.01.2024",
        level = "вузовский",
        types = "научное, творческое",
        location = "Main Hall",
        format = "очный",
        participantsTotal = 100,
        participantsOther = 10,
        participantsSpo = 20,
        participantsVo = 60,
        participantsForeign = 10,
        organizationRole = "организация мероприятия",
        organizations = "иасид",
        description = "A test event description."
    )

    @Test
    fun `toEvent should convert valid EventRaw to Event`() {
        val expectedEvent = Event(
            title = "Test Event",
            description = "A test event description.",
            createdAt = LocalDateTime.now(), // This will be different, so we don't compare it.
            startedAt = LocalDateTime.of(2024, 1, 1, 9, 0),
            endedAt = LocalDateTime.of(2024, 1, 1, 18, 0),
            level = EventLevel.university,
            location = "Main Hall",
            participantsTotal = 100,
            participantsOther = 10,
            participantsSpo = 20,
            participantsVo = 60,
            participantsForeign = 10,
            format = EventFormat.offline,
            organizationRole = OrganizationRole.organization,
            participants = emptyList(),
            types = listOf(EventType.scientefic_educational, EventType.cultural_creative).sortedBy { it.name },
            organizations = listOf(Organization(id = 0, name = "Институт архитектуры, строительства и дизайна КБГУ"))
        )

        val result = validEventRaw.toEvent()

        // Compare all fields except createdAt
        assertEquals(expectedEvent.title, result.title)
        assertEquals(expectedEvent.description, result.description)
        assertEquals(expectedEvent.startedAt, result.startedAt)
        assertEquals(expectedEvent.endedAt, result.endedAt)
        assertEquals(expectedEvent.level, result.level)
        assertEquals(expectedEvent.location, result.location)
        assertEquals(expectedEvent.participantsTotal, result.participantsTotal)
        assertEquals(expectedEvent.participantsOther, result.participantsOther)
        assertEquals(expectedEvent.participantsSpo, result.participantsSpo)
        assertEquals(expectedEvent.participantsVo, result.participantsVo)
        assertEquals(expectedEvent.participantsForeign, result.participantsForeign)
        assertEquals(expectedEvent.format, result.format)
        assertEquals(expectedEvent.organizationRole, result.organizationRole)
        assertEquals(expectedEvent.participants, result.participants)
        assertEquals(expectedEvent.types, result.types.sortedBy { it.name })
        assertEquals(expectedEvent.organizations, result.organizations)
    }

    @Test
    fun `toEvent should throw exception for invalid date`() {
        val eventRaw = validEventRaw.copy(date = "invalid-date")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("there is no such date format \"invalid-date\"", exception.message)
    }

    @Test
    fun `toEvent should throw exception for invalid level`() {
        val eventRaw = validEventRaw.copy(level = "invalid-level")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("level \"invalid-level\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for invalid format`() {
        val eventRaw = validEventRaw.copy(format = "invalid-format")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("format \"invalid-format\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for invalid organizationRole`() {
        val eventRaw = validEventRaw.copy(organizationRole = "invalid-role")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("organization role \"invalid-role\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for invalid event type`() {
        val eventRaw = validEventRaw.copy(types = "invalid-type")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("event type \"invalid-type\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for empty event types`() {
        val eventRaw = validEventRaw.copy(types = "")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("event type \"\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for invalid organization`() {
        val eventRaw = validEventRaw.copy(organizations = "invalid-org")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("organization \"invalid-org\" not found", exception.message)
    }

    @Test
    fun `toEvent should throw exception for empty organizations`() {
        val eventRaw = validEventRaw.copy(organizations = "")
        val exception = assertThrows<IllegalArgumentException> {
            eventRaw.toEvent()
        }
        assertEquals("organization \"\" not found", exception.message)
    }
}