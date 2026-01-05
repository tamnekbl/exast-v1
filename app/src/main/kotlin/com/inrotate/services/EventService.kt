package com.inrotate.services

import com.inrotate.db.suspendTransaction
import com.inrotate.models.Event
import com.inrotate.models.EventRequest
import com.inrotate.repository.EventRepository
import com.inrotate.repository.OrganizationRepository

class EventService(
    private val eventRepository: EventRepository,
    private val organizationRepository: OrganizationRepository,
) {
    suspend fun createEventWithOrganizations(request: EventRequest): Event {
        return suspendTransaction {

            // Retrieve organizations to populate the response
            val organizations = request.organizations.mapNotNull { organizationId ->
                organizationRepository.getById(organizationId)
            }

            // Create the event
            val event = request
                .toEvent()
                .copy(organizations = organizations)

            eventRepository.add(event)
        }
    }
}