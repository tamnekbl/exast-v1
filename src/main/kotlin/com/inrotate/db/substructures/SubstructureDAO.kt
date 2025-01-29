package com.inrotate.db.substructures

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object SubstructuresTable : IdTable<String>("substructures") {
    override val id = varchar("id", 50).entityId() // ID как EntityID
    val name = varchar("name", 255)
    val description = text("description")
}


class SubstructureDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, SubstructureDAO>(SubstructuresTable)

    var name by SubstructuresTable.name
    var description by SubstructuresTable.description

    // Преобразование DAO в модель
    fun toSubstructure() = Substructure(
        id = id.value,
        name = name,
        description = description
    )
}