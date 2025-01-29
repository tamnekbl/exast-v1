package com.inrotate.repository

import com.inrotate.db.BaseStructure
import com.inrotate.db.events.Event
import com.inrotate.db.structures.Structure
import com.inrotate.db.substructures.Substructure


interface EventRepository {
    suspend fun getAll(): List<Event>
    suspend fun getByTimeRange(start: String, end: String): List<Event>
    suspend fun getByName(name: String): Event?
    suspend fun getById(id: Long): Event?
    suspend fun add(event: Event)
    suspend fun edit(id: Long, event: Event)
    suspend fun remove(id: Long)
    suspend fun getFiltered(name: String?, startDate: String?, endDate: String?): List<Event>
}

interface BaseStructureRepository<T : BaseStructure> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: String): T?
    suspend fun getFiltered(name: String?): List<T>
    suspend fun add(structure: T)
    suspend fun edit(id: String, structure: T)
    suspend fun remove(id: String)
}

interface StructureRepository : BaseStructureRepository<Structure> {
}

interface SubstructureRepository : BaseStructureRepository<Substructure> {
}