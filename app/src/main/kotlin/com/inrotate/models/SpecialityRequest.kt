package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class SpecialityRequest(
    val code: String,
    val name: String
) {
    fun toSpeciality(id: Int = 0) = Speciality(
        id = id,
        code = this.code,
        name = this.name
    )
}

