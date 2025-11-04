package com.inrotate.db

import com.inrotate.models.Specialty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object SpecialtiesTable : IntIdTable("specialties") {
    val name = text("name")
}

class SpecialtyDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SpecialtyDAO>(SpecialtiesTable)

    var name by SpecialtiesTable.name
    var participants by ParticipantDAO optionalReferencedOn ParticipantsTable.specialityId

    fun toSpecialty(): Specialty = Specialty(
        id = id.value,
        name = name
    )
}