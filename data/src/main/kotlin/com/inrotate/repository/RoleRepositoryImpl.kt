package com.inrotate.repository

import com.inrotate.db.EventTypeDAO
import com.inrotate.db.RoleDAO
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Role

class RoleRepositoryImpl : RoleRepository {
    override suspend fun getAll(): List<Role> = suspendTransaction {
        RoleDAO.all().map { it.toRole() }
    }

    override suspend fun getById(id: Int): Role? = suspendTransaction {
        RoleDAO.findById(id)?.toRole()
    }

    override suspend fun add(entity: Role): Role = suspendTransaction {
        RoleDAO.new {
            name = entity.name
        }.toRole()
    }

    override suspend fun update(entity: Role): Role = suspendTransaction {
        RoleDAO.findByIdAndUpdate(entity.id) {
            it.name = entity.name
        }?.toRole() ?: throw Exception("Role Not Found")
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        EventTypeDAO.findById(id)?.delete() != null
    }
}