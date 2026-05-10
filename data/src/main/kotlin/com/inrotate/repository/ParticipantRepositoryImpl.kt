package com.inrotate.repository

import com.inrotate.db.OrganizationDAO
import com.inrotate.db.ParticipantDAO
import com.inrotate.db.SpecialityDAO
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Participant

class ParticipantRepositoryImpl : ParticipantRepository {
    override suspend fun getAll(): List<Participant> = suspendTransaction {
        ParticipantDAO.all().map { it.toParticipant() }
    }

    override suspend fun getById(id: Int): Participant? = suspendTransaction {
        ParticipantDAO.findById(id)?.toParticipant()
    }

    override suspend fun add(entity: Participant): Participant = suspendTransaction {
        val participantDAO = ParticipantDAO.new {
            lastName = entity.lastName
            firstName = entity.firstName
            middleName = entity.middleName
            course = entity.course?.toShort()
            studyMode = entity.studyMode
        }

        entity.speciality?.let {
            participantDAO.speciality = SpecialityDAO.findById(it.id)
                ?: throw Exception("Speciality Not Found")
        }

        entity.structure?.let {
            participantDAO.structure = OrganizationDAO.findById(it.id)
                ?: throw Exception("Organization Not Found")
        }

        participantDAO.toParticipant()
    }

    override suspend fun update(entity: Participant): Participant = suspendTransaction {
        val participantDAO = ParticipantDAO.findByIdAndUpdate(entity.id) {
            it.lastName = entity.lastName
            it.firstName = entity.firstName
            it.middleName = entity.middleName
            it.course = entity.course?.toShort()
            it.studyMode = entity.studyMode
        } ?: throw Exception("Participant Not Found")

        entity.speciality?.let {
            participantDAO.speciality = SpecialityDAO.findById(it.id)
                ?: throw Exception("Speciality Not Found")
        }

        entity.structure?.let {
            participantDAO.structure = OrganizationDAO.findById(it.id)
                ?: throw Exception("Organization Not Found")
        }

        participantDAO.toParticipant()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        ParticipantDAO.findById(id)?.delete() != null
    }

    override suspend fun getByLastName(lastName: String): List<Participant> {
        TODO("Not yet implemented")
    }

    override suspend fun getByCourse(course: Int): List<Participant> {
        TODO("Not yet implemented")
    }
}