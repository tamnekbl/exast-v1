package com.inrotate.models

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantRequest(
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val course: Int?,
    val speciality: Int?,
    val structure: Int?
) {
    fun toParticipant(id: Int = 0) = Participant(
        id = id,
        lastName = this.lastName,
        firstName = this.firstName,
        middleName = this.middleName,
        course = this.course,
        speciality = this.speciality?.let { Speciality(it) },
        structure = this.structure?.let { Organization(it) }
    )

}


