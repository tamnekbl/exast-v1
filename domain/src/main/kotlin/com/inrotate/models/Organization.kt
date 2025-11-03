package com.inrotate.models


data class Organization(
    val id: Int,
    val name: String,
    val description: String?,
    val type: OrganizationType?
)

data class OrganizationType(
    val id: Int,
    val name: String
)