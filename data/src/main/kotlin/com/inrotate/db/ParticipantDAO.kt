package com.inrotate.db

import com.inrotate.models.Participant
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
    val specialityId = reference("speciality_id", SpecialtiesTable, onDelete = ReferenceOption.NO_ACTION).nullable()
    val structureId = reference("structure_id", OrganizationsTable, onDelete = ReferenceOption.NO_ACTION).nullable()
}

class ParticipantDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParticipantDAO>(ParticipantsTable)

    var lastName by ParticipantsTable.lastName
    var firstName by ParticipantsTable.firstName
    var middleName by ParticipantsTable.middleName
    var course by ParticipantsTable.course
    var speciality by SpecialtyDAO optionalReferencedOn ParticipantsTable.specialityId
    var structure by OrganizationDAO optionalReferencedOn ParticipantsTable.structureId
    var events by EventDAO via EventParticipantsTable

    fun toParticipant() = Participant(
        id = id.value,
        lastName = lastName,
        firstName = firstName,
        middleName = middleName,
        course = course?.toInt(),
        speciality = speciality?.toSpecialty(),
        structure = structure?.toOrganization()
    )
}




