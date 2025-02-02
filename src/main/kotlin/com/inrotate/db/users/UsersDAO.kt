package com.inrotate.db.users

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object UsersTable : IdTable<String>("users") {
    override val id = varchar("id", 50).entityId() // ID как EntityID
    val name = varchar("name", 255)
    val email = varchar("email", 255)
}


class UsersDAO(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, UsersDAO>(UsersTable)

    var name by UsersTable.name
    var email by UsersTable.email

    // Преобразование DAO в модель
    fun toUser() = User(
        id = id.value,
        name = name,
        email = email
    )
}