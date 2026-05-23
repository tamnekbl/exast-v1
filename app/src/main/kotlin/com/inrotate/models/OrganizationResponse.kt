package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class OrganizationResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String?,
    val isExternal: Boolean
)

fun Organization.toResponse() = OrganizationResponse(
    id = this.id,
    name = this.name,
    description = this.description,
    type = this.type?.type,
    isExternal = this.isExternal
)