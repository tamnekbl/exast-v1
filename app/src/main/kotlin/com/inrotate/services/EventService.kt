package com.inrotate.services

import com.inrotate.db.suspendTransaction
import com.inrotate.models.Event
import com.inrotate.models.EventRequest
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationEventRepository
import com.inrotate.repository.OrganizationRepository

class EventService(
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
    private val organizationEventRepository: OrganizationEventRepository
) {
    suspend fun createEventWithOrganizations(request: EventRequest): Event {
        return suspendTransaction {
            // Create the event
            val event = request.toEvent()
            val createdEvent = eventRepository.add(event)

            // Associate organizations with the event
            request.organizations.forEach { organizationId ->
                organizationEventRepository.addOrganizationToEvent(createdEvent.id, organizationId)
            }

            // Retrieve organizations to populate the response
            val organizations = request.organizations.mapNotNull { organizationId ->
                organizationRepository.getById(organizationId)
            }

            createdEvent.copy(organizations = organizations)
        }
    }
}