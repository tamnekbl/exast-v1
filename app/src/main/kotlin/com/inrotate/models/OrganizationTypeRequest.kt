package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationTypeRequest(
    val type: String
) {
    fun toOrganizationType(id: Int = 0) = OrganizationType(
        id = id,
        type = this.type,
    )
}
