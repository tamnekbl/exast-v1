package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class RoleResponse(
    val id: Int,
    val name: String
)

fun Role.toResponse() = RoleResponse(
    id = this.id,
    name = this.name
)