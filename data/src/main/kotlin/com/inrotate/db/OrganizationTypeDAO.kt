package com.inrotate.db

import com.inrotate.models.OrganizationType
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object OrganizationTypesTable : IntIdTable("organization_type") {
    val type = varchar("type", 255)
}

class OrganizationTypeDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrganizationTypeDAO>(OrganizationTypesTable)

    var type by OrganizationTypesTable.type
    val organizations by OrganizationDAO optionalReferrersOn OrganizationsTable.typeId

    fun toOrganizationType(): OrganizationType = OrganizationType(
        id = id.value,
        type = type
    )
}
