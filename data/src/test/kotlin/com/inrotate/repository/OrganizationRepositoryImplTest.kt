package com.inrotate.repository

import com.inrotate.TestDatabase
import com.inrotate.models.Organization
import com.inrotate.models.OrganizationType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrganizationRepositoryImplTest : TestDatabase() {
    private lateinit var repository: OrganizationRepository
    private lateinit var organizationTypeRepository: OrganizationTypeRepository

    override fun setupDatabase() {
        super.setupDatabase()
        repository = OrganizationRepositoryImpl()
        organizationTypeRepository = OrganizationTypeRepositoryImpl()
    }

    @Test
    fun `should add and retrieve organization`() = runBlocking {
        // Создаем тестовую организацию
        val organization = Organization(
            id = 0,
            name = "Test Organization",
            description = "Test Organization Full Name",
            type = OrganizationType(0, "Educational"),
            isExternal = true,
        )

        assertThrows<Exception> {
            repository.add(organization)
        }

        // Добавляем тип для проверки совместной транзакции
        organizationTypeRepository.add(OrganizationType(1, "Educational"))

        // Добавляем организацию
        val addedOrganization = repository.add(organization)

        // Проверяем, что организация была добавлена
        assertNotNull(addedOrganization.id)
        assertEquals(organization.name, addedOrganization.name)
        assertEquals(organization.description, addedOrganization.description)
        assertEquals(organization.type?.type, addedOrganization.type?.type)
        assertEquals(1, addedOrganization.type?.id)
        assertEquals(organization.isExternal, addedOrganization.isExternal)

        // Получаем организацию по ID
        val retrievedOrganization = repository.getById(addedOrganization.id)

        // Проверяем, что организация корректно извлечена
        assertNotNull(retrievedOrganization)
        assertEquals(addedOrganization.id, retrievedOrganization.id)
        assertEquals(addedOrganization.name, retrievedOrganization.name)
        assertEquals(addedOrganization.type?.type, "Educational")
        assertEquals(addedOrganization.type?.id, 1)
        assertEquals(addedOrganization.isExternal, retrievedOrganization.isExternal)
    }

    @Test
    fun `should update organization`() = runBlocking {
        // Создаем и добавляем организацию
        val originalOrganization = Organization(
            id = 0,
            name = "Test Organization",
            description = "Test Organization Full Name",
            type = OrganizationType(1, "Educational")
        )
        // Добавляем тип для проверки совместной транзакции
        organizationTypeRepository.add(OrganizationType(1, "Educational"))

        val addedOrganization = repository.add(originalOrganization)

        // Обновляем организацию
        val updatedOrganization = addedOrganization.copy(
            name = "Updated Organization",
            description = "Updated Organization Full Name",
            isExternal = true,
        )

        val result = repository.update(updatedOrganization)

        // Проверяем, что организация была обновлена
        assertNotNull(result)
        assertEquals(updatedOrganization.name, result.name)
        assertEquals(updatedOrganization.description, result.description)
        assertEquals(updatedOrganization.isExternal, result.isExternal)
    }

    @Test
    fun `should delete organization`() = runBlocking {
        // Создаем и добавляем организацию
        val organization = Organization(
            id = 0,
            name = "Test Organization",
            description = "Test Organization Full Name",
            type = OrganizationType(1, "Educational")
        )

        // Добавляем тип для проверки совместной транзакции
        organizationTypeRepository.add(OrganizationType(1, "Educational"))

        val addedOrganization = repository.add(organization)
        val organizationId = addedOrganization.id

        // Удаляем организацию
        val deleted = repository.delete(organizationId)

        // Проверяем, что организация была удалена
        assertTrue(deleted)

        // Пытаемся получить удаленную организацию
        val retrievedOrganization = repository.getById(organizationId)
        assertNull(retrievedOrganization)
    }

    @Test
    fun `should get all organizations`() = runBlocking {
        // Добавляем несколько организаций
        val org1 = Organization(
            id = 0,
            name = "Organization 1",
            description = "Organization 1 Full Name",
            type = OrganizationType(1, "Educational")
        )

        // Добавляем тип для проверки совместной транзакции
        organizationTypeRepository.add(OrganizationType(1, "Educational"))

        val org2 = org1.copy(id = 0, name = "Organization 2", description = "Organization 2 Full Name")

        repository.add(org1)
        repository.add(org2)

        // Получаем все организации
        val organizations = repository.getAll()

        // Проверяем, что все организации были получены
        assertEquals(2, organizations.size)
        assertTrue(organizations.any { it.name == "Organization 1" })
        assertTrue(organizations.any { it.name == "Organization 2" })
    }

    @Test
    fun `should filter organizations by name`() = runBlocking {
        val org1 = Organization(
            id = 0,
            name = "Alpha Organization",
            description = "Alpha Organization Full Name",
            type = OrganizationType(1, "Educational")
        )
        val org2 = org1.copy(id = 0, name = "Beta Organization", description = "Beta Organization Full Name")

        organizationTypeRepository.add(OrganizationType(1, "Educational"))

        repository.add(org1)
        repository.add(org2)

        val organizations = repository.getFiltered("alpha")

        assertEquals(1, organizations.size)
        assertEquals("Alpha Organization", organizations.single().name)
    }
}
