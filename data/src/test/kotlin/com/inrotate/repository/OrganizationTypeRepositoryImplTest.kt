package com.inrotate.repository

import com.inrotate.TestDatabase
import com.inrotate.models.OrganizationType
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrganizationTypeRepositoryImplTest : TestDatabase() {
    private lateinit var repository: OrganizationTypeRepository

    override fun setupDatabase() {
        super.setupDatabase()
        repository = OrganizationTypeRepositoryImpl()
    }

    @Test
    fun `should add and retrieve organization type`() = runBlocking {
        // Создаем тестовый тип организации
        val orgType = OrganizationType(
            id = 0,
            name = "University",
        )

        // Добавляем тип организации
        val addedOrgType = repository.add(orgType)

        // Проверяем, что тип организации был добавлен
        assertNotNull(addedOrgType.id)
        assertEquals(orgType.name, addedOrgType.name)

        // Получаем тип организации по ID
        val retrievedOrgType = repository.getById(addedOrgType.id)

        // Проверяем, что тип организации корректно извлечен
        assertNotNull(retrievedOrgType)
        assertEquals(addedOrgType.id, retrievedOrgType.id)
        assertEquals(orgType.name, retrievedOrgType.name)
    }

    @Test
    fun `should update organization type`() = runBlocking {
        // Создаем и добавляем тип организации
        val originalOrgType = OrganizationType(
            id = 0,
            name = "College",
        )

        val addedOrgType = repository.add(originalOrgType)

        // Обновляем тип организации
        val updatedOrgType = addedOrgType.copy(
            name = "University",
        )

        val result = repository.update(updatedOrgType)

        // Проверяем, что тип организации был обновлен
        assertNotNull(result)
        assertEquals("University", result.name)
    }

    @Test
    fun `should delete organization type`() = runBlocking {
        // Создаем и добавляем тип организации
        val orgType = OrganizationType(
            id = 0,
            name = "Institute",
        )

        val addedOrgType = repository.add(orgType)
        val orgTypeId = addedOrgType.id

        // Удаляем тип организации
        val deleted = repository.delete(orgTypeId)

        // Проверяем, что тип организации был удален
        assertTrue(deleted)

        // Пытаемся получить удаленный тип организации
        val retrievedOrgType = repository.getById(orgTypeId)
        assertNull(retrievedOrgType)
    }

    @Test
    fun `should get all organization types`() = runBlocking {
        // Добавляем несколько типов организаций
        val type1 = OrganizationType(
            id = 0,
            name = "University",
        )

        val type2 = OrganizationType(
            id = 0,
            name = "College",
        )

        repository.add(type1)
        repository.add(type2)

        // Получаем все типы организаций
        val orgTypes = repository.getAll()

        // Проверяем, что все типы организаций были получены
        assertEquals(2, orgTypes.size)
        assertTrue(orgTypes.any { it.name == "University" })
        assertTrue(orgTypes.any { it.name == "College" })
    }
}