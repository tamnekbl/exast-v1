package com.inrotate.repository

import com.inrotate.db.*
import com.inrotate.models.Organization
import com.inrotate.models.OrganizationType
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

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
            typeId = entity.type?.id
        }.toOrganization()
    }

    override suspend fun update(entity: Organization): Organization? = suspendTransaction {
        OrganizationDAO.findByIdAndUpdate(entity.id) {
            it.name = entity.name
            it.description = entity.description
            it.typeId = entity.type?.id
        }?.toOrganization()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        OrganizationDAO.findById(id)?.delete() != null
    }

    override suspend fun getByType(typeId: Int): List<Organization> = suspendTransaction {
        OrganizationDAO.find { OrganizationsTable.typeId eq typeId }.map { it.toOrganization() }
    }
}

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

    override suspend fun update(entity: OrganizationType): OrganizationType? = suspendTransaction {
        OrganizationTypeDAO.findByIdAndUpdate(entity.id) {
            it.name = entity.name
        }?.toOrganizationType()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        OrganizationTypeDAO.findById(id)?.delete() != null
    }
}

class OrganizationEventRepositoryImpl : OrganizationEventRepository {
    override suspend fun addOrganizationToEvent(eventId: Int, organizationId: Int): Boolean = suspendTransaction {
        try {
            EventOrganizationDAO.new {
                this.eventId = eventId
                this.organizationId = organizationId
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeOrganizationFromEvent(eventId: Int, organizationId: Int): Boolean = suspendTransaction {
        EventOrganizationsTable.deleteWhere {
            (EventOrganizationsTable.eventId eq eventId) and (EventOrganizationsTable.organizationId eq organizationId)
        } > 0
    }

    override suspend fun getOrganizations(eventId: Int): List<Organization> = suspendTransaction {
        EventOrganizationDAO
            .find { EventOrganizationsTable.eventId eq eventId }
            .mapNotNull { dao ->
                OrganizationDAO.findById(dao.organizationId)?.toOrganization()
            }
    }
}

fun OrganizationDAO.toOrganization(): Organization {
    val type = this.typeId?.let { typeId ->
        OrganizationTypeDAO.findById(typeId)?.toOrganizationType()
    }

    return Organization(
        id = this.id.value,
        name = this.name,
        description = this.description,
        type = type
    )
}

fun OrganizationTypeDAO.toOrganizationType(): OrganizationType {
    return OrganizationType(
        id = this.id.value,
        name = this.name
    )
}