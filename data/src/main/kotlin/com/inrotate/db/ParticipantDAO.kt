package com.inrotate.db

import com.inrotate.models.Participant
import com.inrotate.models.StudyMode
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object ParticipantsTable : IntIdTable("participants") {
    val lastName = varchar("last_name", 255)
    val firstName = varchar("first_name", 255)
    val middleName = varchar("middle_name", 255).nullable()
    val course = short("course").nullable()
    val specialityId = reference("speciality_id", SpecialitiesTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val structureId = reference("structure_id", OrganizationsTable, onDelete = ReferenceOption.SET_NULL).nullable()
    val studyMode = pgEnum<StudyMode>("study_mode", "study_mode").nullable()
}

class ParticipantDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParticipantDAO>(ParticipantsTable)

    var lastName by ParticipantsTable.lastName
    var firstName by ParticipantsTable.firstName
    var middleName by ParticipantsTable.middleName
    var course by ParticipantsTable.course
    var speciality by SpecialityDAO optionalReferencedOn ParticipantsTable.specialityId
    var structure by OrganizationDAO optionalReferencedOn ParticipantsTable.structureId
    var events by EventDAO via ParticipationTable
    var studyMode by ParticipantsTable.studyMode

    fun toParticipant() = Participant(
        id = id.value,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        course = course?.toInt(),
        speciality = speciality?.toSpeciality(),
        structure = structure?.toOrganization(),
        studyMode = studyMode
    )
}




