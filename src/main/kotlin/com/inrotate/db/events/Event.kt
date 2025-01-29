package com.inrotate.db.events

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
