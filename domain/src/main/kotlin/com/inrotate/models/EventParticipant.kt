package com.inrotate.models

data class EventParticipant(
    val eventId: Int,
    val participant: Participant,
    val role: Role
)

data class Role(
    val id: Int,
    val name: String
)
