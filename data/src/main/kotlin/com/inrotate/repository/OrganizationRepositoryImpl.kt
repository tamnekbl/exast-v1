package com.inrotate.repository

import com.inrotate.db.*
import com.inrotate.models.Organization
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.lowerCase

class OrganizationRepositoryImpl : OrganizationRepository {
    override suspend fun getAll(): List<Organization> = suspendTransaction {
        OrganizationDAO.all().map { it.toOrganization() }
    }

    override suspend fun getFiltered(name: String?): List<Organization> = suspendTransaction {
        val filter = if (name.isNullOrBlank()) {
            Op.TRUE
        } else {
            OrganizationsTable.name.lowerCase() like "%${name.lowercase()}%"
        }

        OrganizationDAO
            .find(filter)
            .toList()
            .map { it.toOrganization() }
    }

    override suspend fun getById(id: Int): Organization? = suspendTransaction {
        OrganizationDAO.findById(id)?.toOrganization()
    }

    override suspend fun add(entity: Organization): Organization = suspendTransaction {
        val organizationDAO = OrganizationDAO.new {
            name = entity.name
            description = entity.description
            isExternal = entity.isExternal
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
            o.isExternal = entity.isExternal
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

