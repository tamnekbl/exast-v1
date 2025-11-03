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

enum class EventFormat {
    online, offline, hybrid
}

//отношение организатора к мероприятию
enum class OrganizationRole {
    participation, organization, assistance
}

enum class EventLevel {
    undefined,
    structural,
    intra_university,
    municipal,
    regional,
    interregional,
    district,
    national,
    international
}

enum class EventType(val value: String) {
    CIVIC("гражданское"),
    PATRIOTIC("патриотическое"),
    PHYSICAL("физическое"),
    SPIRITUAL_MORAL("духовно-нравственное"),
    ECOLOGICAL("экологическое"),
    PROFESSIONAL_LABOR("профессионально-трудовое"),
    CULTURAL_CREATIVE("культурно-творческое"),
    SCIENTIFIC_EDUCATIONAL("научно-образовательное"),
    VOLUNTEERING("добровольческое"),
    STUDENT_SELF_GOVERNMENT("студенческое самоуправление");
}