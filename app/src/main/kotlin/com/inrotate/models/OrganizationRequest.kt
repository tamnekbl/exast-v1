package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationRequest (
    val name: String,
    val description: String?,
    val type: String?
) {
    fun toOrganization(id: Int = 0) = Organization(
        id = id,
        name = this.name,
        description = this.description,
        type = this.type?.let { OrganizationType(0, it) }
    )
}
