package com.inrotate

import com.inrotate.db.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class TestDatabase {

    @BeforeEach
    open fun setupDatabase() {
        // Используем in-memory H2 database для тестов
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(
                EventsTable,
                EventTypesTable,
                OrganizationsTable,
                OrganizationsEventsTable,
                EventEventTypesTable,
                ParticipantsTable,
                RolesTable,
                EventParticipantsTable,
                OrganizationTypesTable,
                SpecialitiesTable
            )
        }
    }

    @AfterEach
    open fun tearDownDatabase() {
        transaction {
            SchemaUtils.drop(
                EventsTable,
                EventTypesTable,
                OrganizationsTable,
                OrganizationsEventsTable,
                EventEventTypesTable,
                ParticipantsTable,
                RolesTable,
                EventParticipantsTable,
                OrganizationTypesTable,
                SpecialitiesTable
            )
        }
    }
}