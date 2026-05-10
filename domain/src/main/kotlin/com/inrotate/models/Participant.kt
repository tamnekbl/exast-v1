package com.inrotate.models

data class Participant(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val course: Int?,
    val speciality: Speciality?,
    val structure: Organization?,
    val studyMode: StudyMode?
)

data class Speciality(
    val id: Int,
    val code: String = "",
    val name: String = ""
)

enum class StudyMode(val value: String) {
    FULL_TIME("ОФО"),
    PART_TIME("ОЗФО"),
    DISTANCE("ЗФО")
}