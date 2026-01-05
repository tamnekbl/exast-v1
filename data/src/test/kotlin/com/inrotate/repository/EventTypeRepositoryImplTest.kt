package com.inrotate.repository

/*
@Disabled("Тест не готов, требует доработки")
class EventTypeRepositoryImplTest : TestDatabase() {
    private lateinit var repository: EventTypeRepository
    
    override fun setupDatabase() {
        super.setupDatabase()
        repository = EventTypeRepositoryImpl()
    }
    
    @Test
    fun `should add and retrieve event type`() = runBlocking {
        // Создаем тестовый тип события
        val eventType = EventType(
            id = 0,
            name = "Конференция",
            value = "conference"
        )
        
        // Добавляем тип события
        val addedEventType = repository.add(eventType)
        
        // Проверяем, что тип события был добавлен
        assertNotNull(addedEventType.id)
        assertEquals(eventType.name, addedEventType.name)
        assertEquals(eventType.value, addedEventType.value)
        
        // Получаем тип события по ID
        val retrievedEventType = repository.getById(addedEventType.id)
        
        // Проверяем, что тип события корректно извлечен
        assertNotNull(retrievedEventType)
        assertEquals(addedEventType.id, retrievedEventType.id)
        assertEquals(eventType.name, retrievedEventType.name)
    }
    
    @Test
    fun `should update event type`() = runBlocking {
        // Создаем и добавляем тип события
        val originalEventType = EventType(
            id = 0,
            name = "Семинар",
            value = "seminar"
        )
        
        val addedEventType = repository.add(originalEventType)
        
        // Обновляем тип события
        val updatedEventType = addedEventType.copy(
            name = "Мастер-класс",
            value = "masterclass"
        )
        
        val result = repository.update(updatedEventType)
        
        // Проверяем, что тип события был обновлен
        assertNotNull(result)
        assertEquals("Мастер-класс", result.name)
        assertEquals("masterclass", result.value)
    }
    
    @Test
    fun `should delete event type`() = runBlocking {
        // Создаем и добавляем тип события
        val eventType = EventType(
            id = 0,
            name = "Вебинар",
            value = "webinar"
        )
        
        val addedEventType = repository.add(eventType)
        val eventTypeId = addedEventType.id
        
        // Удаляем тип события
        val deleted = repository.delete(eventTypeId)
        
        // Проверяем, что тип события был удален
        assertTrue(deleted)
        
        // Пытаемся получить удаленный тип события
        val retrievedEventType = repository.getById(eventTypeId)
        assertNull(retrievedEventType)
    }
    
    @Test
    fun `should get all event types`() = runBlocking {
        // Добавляем несколько типов событий
        val type1 = EventType(
            id = 0,
            name = "Конференция",
            value = "conference"
        )
        
        val type2 = EventType(
            id = 0,
            name = "Семинар",
            value = "seminar"
        )
        
        repository.add(type1)
        repository.add(type2)
        
        // Получаем все типы событий
        val eventTypes = repository.getAll()
        
        // Проверяем, что все типы событий были получены
        assertEquals(2, eventTypes.size)
        assertTrue(eventTypes.any { it.name == "Конференция" })
        assertTrue(eventTypes.any { it.name == "Семинар" })
    }
}*/
