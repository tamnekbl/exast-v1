package com.inrotate.db

import com.inrotate.models.Role
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RolesTable : IntIdTable("roles") {
    val name = text("name").uniqueIndex()
}

class RoleDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RoleDAO>(RolesTable)

    var name by RolesTable.name
    // при необходимости — roles могут быть связаны с участниками через EventParticipantsTable

    fun toRole() = Role(
        id = id.value,
        name = name
    )
}