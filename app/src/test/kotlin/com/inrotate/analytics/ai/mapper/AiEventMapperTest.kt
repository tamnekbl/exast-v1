package com.inrotate.analytics.ai.mapper

import com.inrotate.analytics.ai.dto.AiEventScalePredictionRequest
import com.inrotate.analytics.ai.dto.EventScale
import com.inrotate.analytics.dto.EventDraftRequest
import com.inrotate.models.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.*

class AiEventMapperTest {
    @Test
    fun `participantsTotal is not serialized in prediction request`() {
        val request = AiEventMapper.fromEvent(testEvent(participantsTotal = 150))

        val json = Json.encodeToString<AiEventScalePredictionRequest>(request)

        assertFalse(json.contains("participantsTotal"))
        assertFalse(json.contains("participants_total"))
    }

    @Test
    fun `prediction request is serialized with python snake case fields`() {
        val request = AiEventMapper.fromEvent(testEvent())

        val json = Json.encodeToString<AiEventScalePredictionRequest>(request)

        assertTrue(json.contains("date_start"))
        assertTrue(json.contains("date_end"))
        assertTrue(json.contains("time_start"))
        assertTrue(json.contains("time_end"))
        assertTrue(json.contains("organization_role"))
        assertFalse(json.contains("dateStart"))
        assertFalse(json.contains("organizationRole"))
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
    fun `event scale enum serializes as python values`() {
        assertEquals(""""small_1_20"""", Json.encodeToString<EventScale>(EventScale.SMALL_1_20))
        assertEquals(""""medium_21_50"""", Json.encodeToString<EventScale>(EventScale.MEDIUM_21_50))
        assertEquals(""""large_51_200"""", Json.encodeToString<EventScale>(EventScale.LARGE_51_200))
        assertEquals(""""mass_201_plus"""", Json.encodeToString<EventScale>(EventScale.MASS_201_PLUS))
    }

    @Test
    fun `empty description does not break draft mapping`() {
        val draft = EventDraftRequest(
            title = "Draft event",
            description = null,
            dateStart = "2026-06-01",
            dateEnd = null,
            timeStart = "10:15",
            timeEnd = null,
            level = EventLevel.university,
            location = null,
            format = EventFormat.offline,
            organizationRole = OrganizationRole.organization,
            types = listOf(EventType.cultural_creative),
            organizations = listOf(67),
        )

        val request = AiEventMapper.fromDraftRequest(
            request = draft,
            organizations = listOf(testOrganization()),
        )

        assertNull(request.description)
        assertEquals("2026-06-01", request.dateStart)
        assertEquals("10:15:00", request.timeStart)
        assertEquals(true, request.organizations.single().isExternal)
    }

    @Test
    fun `event mapping uses stable ai codes and iso date time formats`() {
        val request = AiEventMapper.fromEvent(testEvent())

        assertEquals("2026-06-01", request.dateStart)
        assertEquals("10:15:00", request.timeStart)
        assertEquals("organization", request.organizationRole)
        assertFalse(Json.encodeToString<AiEventScalePredictionRequest>(request).contains("organizer"))
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
