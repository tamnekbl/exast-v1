package com.inrotate.repository

import com.inrotate.db.SpecialityDAO
import com.inrotate.db.suspendTransaction
import com.inrotate.models.Speciality

class SpecialityRepositoryImpl : SpecialityRepository {
    override suspend fun getAll(): List<Speciality> = suspendTransaction {
        SpecialityDAO.all()
            //.sortedBy { it.id.value } //todo в отдельной задаче разобрать для всех DAO
            .map { it.toSpeciality() }
    }

    override suspend fun getById(id: Int): Speciality? = suspendTransaction {
        SpecialityDAO.findById(id)?.toSpeciality()
    }

    override suspend fun add(entity: Speciality): Speciality = suspendTransaction {
        try {
            SpecialityDAO.new {
                name = entity.name
                code = entity.code
            }.toSpeciality()
        } catch (e: Exception) {
            print(e.message)
            throw e
        }

    }

    override suspend fun update(entity: Speciality): Speciality = suspendTransaction {
        SpecialityDAO.findByIdAndUpdate(entity.id) {
            it.name = entity.name
            it.code = entity.code
        }?.toSpeciality() ?: throw Exception("Specialty Not Found")
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction {
        SpecialityDAO.findById(id)?.delete() != null
    }
}