package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    val id: Int,
    val name: String
)

@Serializable
data class ParticipantResponse(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val course: Int?,
    val speciality: String?,
    val structure: OrganizationResponse?
)

@Serializable
data class EventParticipantResponse(
    val eventId: Int,
    val participant: ParticipantResponse,
    val role: RoleResponse
)

fun Role.toResponse() = RoleResponse(
    id = this.id,
    name = this.name
)

fun Participant.toResponse() = ParticipantResponse(
    id = this.id,
    lastName = this.lastName,
    firstName = this.firstName,
    middleName = this.middleName,
    course = this.course,
    speciality = this.speciality?.name,
    structure = this.structure?.toResponse()
)