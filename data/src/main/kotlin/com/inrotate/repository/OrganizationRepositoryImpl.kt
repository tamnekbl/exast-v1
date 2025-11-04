package com.inrotate.repository

import com.inrotate.db.OrganizationDAO
import com.inrotate.db.OrganizationTypeDAO
import com.inrotate.db.OrganizationsTable
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Organization

class OrganizationRepositoryImpl : OrganizationRepository {
    override suspend fun getAll(): List<Organization> = suspendTransaction {
        OrganizationDAO.all().map { it.toOrganization() }
    }

    override suspend fun getById(id: Int): Organization? = suspendTransaction {
        OrganizationDAO.findById(id)?.toOrganization()
    }

    override suspend fun add(entity: Organization): Organization = suspendTransaction {
        OrganizationDAO.new {
            name = entity.name
            description = entity.description
            type = entity.type?.id?.let { OrganizationTypeDAO.findById(it) }
        }.toOrganization()
    }

    override suspend fun update(entity: Organization): Organization? = suspendTransaction {
        OrganizationDAO.findByIdAndUpdate(entity.id) { o ->
            o.name = entity.name
            o.description = entity.description
            o.type = entity.type?.id?.let { OrganizationTypeDAO.findById(it) }
        }?.toOrganization()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        OrganizationDAO.findById(id)?.delete() != null
    }

    override suspend fun getByType(typeId: Int): List<Organization> = suspendTransaction {
        OrganizationDAO.find { OrganizationsTable.typeId eq typeId }.map { it.toOrganization() }
    }
}

