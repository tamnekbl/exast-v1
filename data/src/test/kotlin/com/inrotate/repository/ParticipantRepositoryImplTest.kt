package com.inrotate.repository
/*
import com.inrotate.TestDatabase
import com.inrotate.db.*
import com.inrotate.models.Participant
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Disabled("Тест не готов, требует доработки")
class ParticipantRepositoryImplTest : TestDatabase() {
    private lateinit var repository: ParticipantRepository
    
    override fun setupDatabase() {
        super.setupDatabase()
        repository = ParticipantRepositoryImpl()
    }
    
    @Test
    fun `should add and retrieve participant`() = runBlocking {
        // Создаем тестового участника
        val participant = Participant(
            id = 0,
            lastName = "Иванов",
            firstName = "Иван",
            middleName = "Иванович",
            birthDate = java.time.LocalDate.of(1990, 1, 1),
            courseId = 3,
            specialityId = 1,
            phone = "+7(123)456-78-90",
            email = "ivanov@example.com"
        )
        
        // Добавляем участника
        val addedParticipant = repository.add(participant)
        
        // Проверяем, что участник был добавлен
        assertNotNull(addedParticipant.id)
        assertEquals(participant.lastName, addedParticipant.lastName)
        assertEquals(participant.firstName, addedParticipant.firstName)
        
        // Получаем участника по ID
        val retrievedParticipant = repository.getById(addedParticipant.id)
        
        // Проверяем, что участник корректно извлечен
        assertNotNull(retrievedParticipant)
        assertEquals(addedParticipant.id, retrievedParticipant.id)
        assertEquals(participant.lastName, retrievedParticipant.lastName)
    }
    
    @Test
    fun `should update participant`() = runBlocking {
        // Создаем и добавляем участника
        val originalParticipant = Participant(
            id = 0,
            lastName = "Петров",
            firstName = "Петр",
            middleName = "Петрович",
            birthDate = java.time.LocalDate.of(1992, 5, 15),
            courseId = 2,
            specialityId = 2,
            phone = "+7(123)456-78-91",
            email = "petrov@example.com"
        )
        
        val addedParticipant = repository.add(originalParticipant)
        
        // Обновляем участника
        val updatedParticipant = addedParticipant.copy(
            lastName = "Сидоров",
            firstName = "Сидор"
        )
        
        val result = repository.update(updatedParticipant)
        
        // Проверяем, что участник был обновлен
        assertNotNull(result)
        assertEquals("Сидоров", result.lastName)
        assertEquals("Сидор", result.firstName)
    }
    
    @Test
    fun `should delete participant`() = runBlocking {
        // Создаем и добавляем участника
        val participant = Participant(
            id = 0,
            lastName = "Козлов",
            firstName = "Алексей",
            middleName = "Алексеевич",
            birthDate = java.time.LocalDate.of(1995, 8, 20),
            courseId = 4,
            specialityId = 3,
            phone = "+7(123)456-78-92",
            email = "kozlov@example.com"
        )
        
        val addedParticipant = repository.add(participant)
        val participantId = addedParticipant.id
        
        // Удаляем участника
        val deleted = repository.delete(participantId)
        
        // Проверяем, что участник был удален
        assertTrue(deleted)
        
        // Пытаемся получить удаленного участника
        val retrievedParticipant = repository.getById(participantId)
        assertNull(retrievedParticipant)
    }
    
    @Test
    fun `should get all participants`() = runBlocking {
        // Добавляем несколько участников
        val participant1 = Participant(
            id = 0,
            lastName = "Иванов",
            firstName = "Иван",
            middleName = "Иванович",
            birthDate = java.time.LocalDate.of(1990, 1, 1),
            courseId = 3,
            specialityId = 1,
            phone = "+7(123)456-78-90",
            email = "ivanov@example.com"
        )
        
        val participant2 = Participant(
            id = 0,
            lastName = "Петров",
            firstName = "Петр",
            middleName = "Петрович",
            birthDate = java.time.LocalDate.of(1992, 5, 15),
            courseId = 2,
            specialityId = 2,
            phone = "+7(123)456-78-91",
            email = "petrov@example.com"
        )
        
        repository.add(participant1)
        repository.add(participant2)
        
        // Получаем всех участников
        val participants = repository.getAll()
        
        // Проверяем, что все участники были получены
        assertEquals(2, participants.size)
        assertTrue(participants.any { it.lastName == "Иванов" })
        assertTrue(participants.any { it.lastName == "Петров" })
    }
}*/
