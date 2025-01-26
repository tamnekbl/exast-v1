package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val createdAt: String = "",
    val startedAt: String = "",
    val endedAt: String = "",
    //place, type, category,
)



@Serializable
data class Organizer(
    val id: Long,
    val name: String,
    val phone: String
)

@Serializable
data class Structure(
    val id: Long,
    val name: String,
    val description: String
)

@Serializable
data class Participant(
    val id: Long,
    val name: String,
    val email: String
)
