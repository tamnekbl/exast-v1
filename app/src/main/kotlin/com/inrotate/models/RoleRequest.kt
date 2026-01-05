package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(
    val name: String
) {
    fun toRole(id: Int = 0) = Role(
        id = id,
        name = this.name
    )
}
