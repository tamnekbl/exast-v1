package com.inrotate.models

data class Participant(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val course: Int?,
    val speciality: Specialty?,
    val structure: Organization?
)

data class Specialty(
    val id: Int,
    val name: String
)
