package com.inrotate.repository

import com.inrotate.db.OrganizationTypeDAO
import com.inrotate.db.suspendTransaction
import com.inrotate.models.OrganizationType

class OrganizationTypeRepositoryImpl : OrganizationTypeRepository {
    override suspend fun getAll(): List<OrganizationType> = suspendTransaction {
        OrganizationTypeDAO.all().map { it.toOrganizationType() }
    }

    override suspend fun getById(id: Int): OrganizationType? = suspendTransaction {
        OrganizationTypeDAO.findById(id)?.toOrganizationType()
    }

    override suspend fun add(entity: OrganizationType): OrganizationType = suspendTransaction {
        OrganizationTypeDAO.new {
            name = entity.name
        }.toOrganizationType()
    }

    override suspend fun update(entity: OrganizationType): OrganizationType = suspendTransaction {
        OrganizationTypeDAO.findByIdAndUpdate(entity.id) {
            it.name = entity.name
        }?.toOrganizationType() ?: throw Exception("Organization type Not Found")
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        OrganizationTypeDAO.findById(id)?.delete() != null
    }
}
