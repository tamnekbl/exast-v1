package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class SpecialityResponse(
    val id: Int,
    val code: String,
    val name: String
)

fun Speciality.toResponse() = SpecialityResponse(
    id = this.id,
    code = this.code,
    name = this.name
)