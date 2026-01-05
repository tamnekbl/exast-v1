package com.inrotate.repository

import com.inrotate.db.*
import com.inrotate.models.Organization

class OrganizationRepositoryImpl : OrganizationRepository {
    override suspend fun getAll(): List<Organization> = suspendTransaction {
        OrganizationDAO.all().map { it.toOrganization() }
    }

    override suspend fun getById(id: Int): Organization? = suspendTransaction {
        OrganizationDAO.findById(id)?.toOrganization()
    }

    override suspend fun add(entity: Organization): Organization = suspendTransaction {
        val organizationDAO = OrganizationDAO.new {
            name = entity.name
            description = entity.description
        }

        entity.type?.let {
            organizationDAO.type = OrganizationTypeDAO
                .find { OrganizationTypesTable.type eq it.type }
                .firstOrNull() ?: throw Exception("Organization type not found")
        }

        organizationDAO.toOrganization()
    }

    override suspend fun update(entity: Organization): Organization = suspendTransaction {
        val organizationDAO = OrganizationDAO.findByIdAndUpdate(entity.id) { o ->
            o.name = entity.name
            o.description = entity.description
        } ?: throw Exception("Organization not found")


        entity.type?.let {
            organizationDAO.type = OrganizationTypeDAO
                .find { OrganizationTypesTable.type eq it.type }
                .firstOrNull() ?: throw Exception("Organization type not found")
        }

        organizationDAO.toOrganization()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        OrganizationDAO.findById(id)?.delete() != null
    }

    override suspend fun getByType(typeId: Int): List<Organization> = suspendTransaction {
        OrganizationDAO.find { OrganizationsTable.typeId eq typeId }.map { it.toOrganization() }
    }
}

