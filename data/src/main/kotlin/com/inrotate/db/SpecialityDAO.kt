package com.inrotate.db

import com.inrotate.models.Speciality
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SpecialitiesTable : IntIdTable("specialities") {
    val code = varchar("code", 10)
    val name = text("name")
}

class SpecialityDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SpecialityDAO>(SpecialitiesTable)

    var code by SpecialitiesTable.code
    var name by SpecialitiesTable.name

    val participants by ParticipantDAO optionalReferrersOn ParticipantsTable.specialityId

    fun toSpeciality(): Speciality = Speciality(
        id = id.value,
        name = name,
        code = code
    )
}