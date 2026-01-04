package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationTypeResponse(
    val id: Int,
    val type: String
)

fun OrganizationType.toResponse() = OrganizationTypeResponse(
    id = this.id,
    type = this.type,
)