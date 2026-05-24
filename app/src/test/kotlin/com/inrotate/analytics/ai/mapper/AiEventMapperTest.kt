package com.inrotate.analytics.ai.mapper

import com.inrotate.analytics.ai.dto.AiPredictionRequest
import com.inrotate.models.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class AiEventMapperTest {
    @Test
    fun `participantsTotal is not serialized in prediction request`() {
        val request = AiEventMapper.fromEvent(testEvent(participantsTotal = 150))

        val json = Json.encodeToString<AiPredictionRequest>(request)

        assertFalse(json.contains("participantsTotal"))
        assertFalse(json.contains("participants_total"))
    }

    @Test
    fun `isExternal is mapped for organizations`() {
        val request = AiEventMapper.fromEvent(testEvent())

        assertEquals(true, request.organizations.single().isExternal)
    }

    @Test
    fun `types are serialized as stable enum codes`() {
        val request = AiEventMapper.fromEvent(
            testEvent(types = listOf(EventType.cultural_creative, EventType.physical))
        )

        assertEquals(listOf("cultural_creative", "physical"), request.types)
    }

    @Test
    fun `empty description does not break draft mapping`() {
        val draft = EventRequest(
            title = "Draft event",
            description = null,
            startedAt = "2026-06-01T10:15:00",
            endedAt = null,
            level = EventLevel.university,
            location = null,
            participantsTotal = 0,
            participantsOther = 0,
            participantsSpo = 0,
            participantsVo = 0,
            participantsForeign = 0,
            format = EventFormat.offline,
            organizationRole = OrganizationRole.organization,
            types = listOf(EventType.cultural_creative),
            organizations = listOf(67),
        )

        val request = AiEventMapper.fromEventRequest(
            request = draft,
            organizations = listOf(testOrganization()),
        )

        assertNull(request.description)
        assertEquals("2026-06-01", request.dateStart)
        assertEquals("10:15", request.timeStart)
        assertEquals(true, request.organizations.single().isExternal)
    }

    private fun testEvent(
        participantsTotal: Int = 100,
        types: List<EventType> = listOf(EventType.cultural_creative),
    ): Event = Event(
        id = 1,
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
        organizations = listOf(testOrganization()),
    )

    private fun testOrganization(): Organization = Organization(
        id = 67,
        name = "Первичное отделение Движения Первых КБГУ",
        type = OrganizationType(1, "OTHER"),
        isExternal = true,
    )
}
