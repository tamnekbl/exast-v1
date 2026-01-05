package com.inrotate.models

data class Participant(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val course: Int?,
    val speciality: Speciality?,
    val structure: Organization?
    //todo add форма обучаения ОФО, ЗФО, ОЗФО
)

data class Speciality(
    val id: Int,
    val code: String = "",
    val name: String = ""
)
