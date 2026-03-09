package com.inrotate.models

import java.time.LocalDateTime

data class Event(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val startedAt: LocalDateTime,
    val endedAt: LocalDateTime?,
    val level: EventLevel,
    val location: String?,
    val participantsTotal: Int,
    val participantsOther: Int,
    val participantsSpo: Int,
    val participantsVo: Int,
    val participantsForeign: Int,
    val format: EventFormat,         // enum
    val organizationRole: OrganizationRole, // enum
    val participants: List<EventParticipant> = emptyList(),
    val types: List<EventType> = emptyList(),
    val organizations: List<Organization> = emptyList()
) {
    // Extension properties for date and time formatting
    val dateStart: String get() = startedAt.toLocalDate().toString()
    val dateEnd: String? get() = endedAt?.toLocalDate()?.toString()
    val timeStart: String get() = startedAt.toLocalTime().toString()
    val timeEnd: String? get() = endedAt?.toLocalTime()?.toString()
}

enum class EventFormat(val value: String) {
    online("онлайн"), offline("очный"), hybrid("смешанный")
}

//отношение организатора к мероприятию
enum class OrganizationRole(val value: String) {
    participation("участие в мероприятии"),
    organization("организация мероприятия"),
    assistance("помощь в организации мероприятия")
}

enum class EventLevel(val value: String) {
    undefined(""),
    structural("структурный"),
    university("вузовский"),
    municipal("муниципальный"),
    regional("региональный"),
    interregional("межрегиональный"),
    district("окружной"),
    national("всероссийский"),
    international("международный")
}

enum class EventType(vararg val aliases: String) {
    civic(
        "гражданское",
        "гражданско-патриотическое"
    ),
    patriotic(
        "патриотическое",
        "гражданско-патриотическое"
    ),
    physical(
        "физическое",
        "спортивное",
        "оздоровительное",
        "туристическое",
        "физкультурное"
    ),
    spiritual_moral(
        "духовно-нравственное"
    ),
    ecological(
        "экологическое"
    ),
    professional_labor(
        "профессионально-трудовое",
        "трудовое",
        "профессиональное"
    ),
    cultural_creative(
        "культурно-просветительское",
        "культурно-творческое",
        "культурное",
        "творческое",
        "развлекательное"
    ),
    scientefic_educational(
        "научно-образовательное",
        "научное",
        "образовательное"
    ),
    volunteering(
        "добровольческое"
    ),
    project_entrepreneurial(
        "проектно-предпринимательское",
        "предпринимательское",
        "проектное"
    ),
    student_self_government(
        "студенческое самоуправление",
        "организационное"
    );

    companion object {
        private val mapping: Map<String, EventType> = entries.flatMap { type ->
            type.aliases.map { it.lowercase() to type }
        }.toMap()

        operator fun invoke(value: String): EventType = mapping[value.lowercase().trim()]
            ?: throw IllegalArgumentException("event type \"${value.trim()}\" not found")

        fun parseRaw(rawTypes: String): List<EventType> =
            rawTypes
                .split(',')
                .map { this(it) }
                .distinct()
    }
}