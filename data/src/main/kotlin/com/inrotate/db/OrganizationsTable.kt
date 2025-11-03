package com.inrotate.db

import com.inrotate.db.events.EventsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object OrganizationsTable : IntIdTable("organizations") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val typeId = integer("type_id").references(OrganizationTypesTable.id, onDelete = ReferenceOption.CASCADE).nullable()
}

object OrganizationTypesTable : IntIdTable("organization_types") {
    val name = varchar("name", 255)
}

object EventOrganizationsTable : IntIdTable("event_organizations") {
    val eventId = integer("event_id").references(EventsTable.id, onDelete = ReferenceOption.CASCADE)
    val organizationId =
        integer("organization_id").references(OrganizationsTable.id, onDelete = ReferenceOption.CASCADE)
}

class OrganizationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrganizationDAO>(OrganizationsTable)

    var name by OrganizationsTable.name
    var description by OrganizationsTable.description
    var typeId by OrganizationsTable.typeId
}

class OrganizationTypeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrganizationTypeDAO>(OrganizationTypesTable)

    var name by OrganizationTypesTable.name
}

class EventOrganizationDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventOrganizationDAO>(EventOrganizationsTable)

    var eventId by EventOrganizationsTable.eventId
    var organizationId by EventOrganizationsTable.organizationId
}