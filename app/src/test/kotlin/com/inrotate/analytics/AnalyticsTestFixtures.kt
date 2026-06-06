package com.inrotate.analytics

import com.inrotate.analytics.ai.dto.*
import com.inrotate.models.*
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository
import kotlinx.serialization.json.JsonPrimitive
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

fun testFeatureInsightsResponse(
    modelVersion: String = "event_scale_v1",
    eventsCount: Int = 42,
): AiFeatureInsightsResponse = AiFeatureInsightsResponse(
    model = AiAnalyticsModelInfo(
        modelVersion = modelVersion,
        modelType = "random_forest",
        taskType = "event_scale",
        trainedAt = "2026-06-01T10:00:00",
        metrics = mapOf("accuracy" to JsonPrimitive(0.71)),
        baselineMetrics = mapOf("accuracy" to JsonPrimitive(0.4)),
        warnings = emptyList(),
    ),
    dataset = AiAnalyticsDataset(
        eventsCount = eventsCount,
        scaleDistribution = listOf(
            AiScaleDistributionItem(
                scale = "mass_201_plus",
                label = "Mass",
                participantsRange = "201+",
                count = 10,
                percent = 23.8,
            ),
        ),
        participantsBuckets = listOf(
            AiParticipantsBucketItem(
                bucket = "201+",
                from = 201,
                to = null,
                count = 10,
                percent = 23.8,
            ),
        ),
    ),
    featureImportance = AiFeatureImportanceBlock(
        topTransformedFeatures = listOf(
            AiFeatureImportanceItem(
                feature = "main_type:cultural_creative",
                displayName = "Cultural creative",
                importance = 0.35,
                group = "main_type",
            ),
        ),
        groupedFeatures = listOf(
            AiFeatureImportanceItem(
                feature = "main_type",
                displayName = "Main type",
                importance = 0.42,
                group = null,
            ),
        ),
    ),
    factors = AiFactorsBlock(
        byMainType = listOf(
            AiFactorStatsItem(
                code = "cultural_creative",
                label = "Cultural creative",
                count = 12,
                meanParticipants = 120.0,
                medianParticipants = 90.0,
                massCount = 3,
                massShare = 0.25,
                largeOrMassShare = 0.5,
                percent = 28.5,
            ),
        ),
    ),
    charts = AiChartsBlock(
        scaleDistribution = AiChartData(
            title = "Scale distribution",
            type = "bar",
            items = listOf(
                AiChartItem(
                    label = "Mass",
                    value = 10.0,
                    percent = 23.8,
                    code = "mass_201_plus",
                ),
            ),
        ),
    ),
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
    override suspend fun getFiltered(name: String?): List<Organization> =
        organizations.filter { organization ->
            name.isNullOrBlank() || organization.name.contains(name, ignoreCase = true)
        }

    override suspend fun getByType(typeId: Int): List<Organization> =
        organizations.filter { it.type?.id == typeId }
}
