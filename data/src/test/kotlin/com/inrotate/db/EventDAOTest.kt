package com.inrotate.db

/*
@Disabled("Тест не готов, требует доработки")
class IgnoredEventDAOTest : TestDatabase()
    
    @Test
    fun `should create and retrieve event dao`() {
        val now = LocalDateTime.now()
        val start = now.plusDays(1)
        val end = now.plusDays(2)
        
        val eventDAO = transaction {
            EventDAO.new {
                title = "Test Event"
                description = "Test Description"
                createdAt = now
                startedAt = start
                endedAt = end
                location = "Test Location"
                participantsTotal = 10
                participantsOther = 2
                participantsSpo = 3
                participantsVo = 4
                participantsForeign = 1
                format = EventFormat.online
                level = EventLevel.international
                organizationRole = OrganizationRole.organizer
            }
        }
        
        // Проверяем, что событие было создано
        assertNotNull(eventDAO.id)
        assertEquals("Test Event", eventDAO.title)
        assertEquals("Test Description", eventDAO.description)
        assertEquals(now, eventDAO.createdAt)
        assertEquals(start, eventDAO.startedAt)
        assertEquals(end, eventDAO.endedAt)
        assertEquals("Test Location", eventDAO.location)
        assertEquals(10, eventDAO.participantsTotal)
        assertEquals(2, eventDAO.participantsOther)
        assertEquals(3, eventDAO.participantsSpo)
        assertEquals(4, eventDAO.participantsVo)
        assertEquals(1, eventDAO.participantsForeign)
        assertEquals(EventFormat.online, eventDAO.format)
        assertEquals(EventLevel.international, eventDAO.level)
        assertEquals(OrganizationRole.organizer, eventDAO.organizationRole)
    }
    
    @Test
    fun `should convert event dao to event model`() {
        val now = LocalDateTime.now()
        val start = now.plusDays(1)
        val end = now.plusDays(2)
        
        val eventDAO = transaction {
            EventDAO.new {
                title = "Test Event"
                description = "Test Description"
                createdAt = now
                startedAt = start
                endedAt = end
                location = "Test Location"
                participantsTotal = 10
                participantsOther = 2
                participantsSpo = 3
                participantsVo = 4
                participantsForeign = 1
                format = EventFormat.online
                level = EventLevel.international
                organizationRole = OrganizationRole.organizer
            }
        }
        
        val event = eventDAO.toEvent()
        
        assertEquals(eventDAO.id.value, event.id)
        assertEquals("Test Event", event.title)
        assertEquals("Test Description", event.description)
        assertEquals(now, event.createdAt)
        assertEquals(start, event.startedAt)
        assertEquals(end, event.endedAt)
        assertEquals("Test Location", event.location)
        assertEquals(10, event.participantsTotal)
        assertEquals(2, event.participantsOther)
        assertEquals(3, event.participantsSpo)
        assertEquals(4, event.participantsVo)
        assertEquals(1, event.participantsForeign)
        assertEquals(EventFormat.online, event.format)
        assertEquals(EventLevel.international, event.level)
        assertEquals(OrganizationRole.organizer, event.organizationRole)
        assertNotNull(event.participants)
        assertNotNull(event.types)
        assertNotNull(event.organizations)
    }
    
    @Test
    fun `should find all events`() {
        // Создаем несколько событий
        transaction {
            EventDAO.new {
                title = "Event 1"
                description = "Description 1"
                createdAt = LocalDateTime.now()
                startedAt = LocalDateTime.now().plusDays(1)
                endedAt = LocalDateTime.now().plusDays(2)
                location = "Location 1"
                participantsTotal = 5
                format = EventFormat.offline
                level = EventLevel.local
                organizationRole = OrganizationRole.participation
            }
            
            EventDAO.new {
                title = "Event 2"
                description = "Description 2"
                createdAt = LocalDateTime.now()
                startedAt = LocalDateTime.now().plusDays(3)
                endedAt = LocalDateTime.now().plusDays(4)
                location = "Location 2"
                participantsTotal = 8
                format = EventFormat.hybrid
                level = EventLevel.regional
                organizationRole = OrganizationRole.coorganizer
            }
        }
        
        // Получаем все события
        val events = transaction {
            EventDAO.all().toList()
        }
        
        // Проверяем, что все события были получены
        assertEquals(2, events.size)
        assertTrue(events.any { it.title == "Event 1" })
        assertTrue(events.any { it.title == "Event 2" })
    }
}*/
