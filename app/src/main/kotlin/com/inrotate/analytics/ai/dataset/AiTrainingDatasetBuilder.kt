package com.inrotate.analytics.ai.dataset

import com.inrotate.analytics.ai.dto.AiOrganizationDto
import com.inrotate.models.Event
import com.inrotate.repository.EventRepository
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class AiTrainingDatasetBuilder(
    private val eventRepository: EventRepository,
    private val json: Json = Json {
        explicitNulls = false
    },
) {
    suspend fun buildCsv(): ByteArray {
        val rows = eventRepository
            .getAll()
            .filter { it.participantsTotal > 0 }
            .map { it.toCsvRow() }

        return buildString {
            appendLine(HEADER.toCsvLine())
            rows.forEach { row ->
                appendLine(row.toCsvLine())
            }
        }.toByteArray(StandardCharsets.UTF_8)
    }

    private fun Event.toCsvRow(): List<String?> = listOf(
        title,
        description,
        startedAt.toLocalDate().toString(),
        endedAt?.toLocalDate()?.toString(),
        startedAt.toLocalTime().toString(),
        endedAt?.toLocalTime()?.toString(),
        level.name,
        format.name,
        organizationRole.name,
        json.encodeToString(types.map { it.name }),
        json.encodeToString(
            organizations.map { organization ->
                AiOrganizationDto(
                    id = organization.id.toLong(),
                    name = organization.name,
                    type = organization.type?.type,
                    isExternal = organization.isExternal,
                )
            },
        ),
        participantsTotal.toString(),
        participantsVo.toString(),
        participantsSpo.toString(),
        participantsForeign.toString(),
        participantsOther.toString(),
    )

    private fun List<String?>.toCsvLine(): String = joinToString(separator = ",") { value ->
        value.toCsvValue()
    }

    private fun String?.toCsvValue(): String {
        if (this == null) {
            return ""
        }

        val shouldQuote = any { it == ',' || it == '"' || it == '\r' || it == '\n' }
        val escaped = replace("\"", "\"\"")
        return if (shouldQuote) "\"$escaped\"" else escaped
    }

    private companion object {
        val HEADER = listOf(
            "title",
            "description",
            "date_start",
            "date_end",
            "time_start",
            "time_end",
            "level",
            "format",
            "organization_role",
            "types",
            "organizations",
            "participants_total",
            "participants_vo",
            "participants_spo",
            "participants_foreign",
            "participants_other",
        )
    }
}
