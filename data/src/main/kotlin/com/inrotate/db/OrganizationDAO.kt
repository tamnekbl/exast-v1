package com.inrotate.db

import com.inrotate.models.Organization
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object OrganizationsTable : IntIdTable("organizations") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val typeId = reference("type_id", OrganizationTypesTable).nullable()
}

object OrganizationsEventsTable : Table("organizations_events") {
    val eventId = reference("event_id", EventsTable.id, onDelete = ReferenceOption.CASCADE)
    val organizationId = reference("organization_id", OrganizationsTable.id, onDelete = ReferenceOption.NO_ACTION)
    override val primaryKey = PrimaryKey(eventId, organizationId)
}

class OrganizationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrganizationDAO>(OrganizationsTable)

    var name by OrganizationsTable.name
    var description by OrganizationsTable.description
    var type by OrganizationTypeDAO optionalReferencedOn OrganizationsTable.typeId

    var events by EventDAO via OrganizationsEventsTable

    fun toOrganization(): Organization = Organization(
        id = id.value,
        name = name,
        description = description,
        type = type?.toOrganizationType()
    )
}