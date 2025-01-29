package com.inrotate.db.structures

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object StructuresTable : IdTable<String>("structures") {
    override val id = varchar("id", 50).entityId() // ID как EntityID
    val name = varchar("name", 255)
    val description = text("description")
}


class StructureDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, StructureDAO>(StructuresTable)

    var name by StructuresTable.name
    var description by StructuresTable.description

    // Преобразование DAO в модель
    fun toStructure() = Structure(
        id = id.value,
        name = name,
        description = description
    )
}