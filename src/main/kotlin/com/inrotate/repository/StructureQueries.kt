package com.inrotate.repository


import com.inrotate.db.structures.Structure
import com.inrotate.db.structures.StructureDAO
import com.inrotate.db.structures.StructuresTable
import com.inrotate.db.suspendTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class StructureQueries : StructureRepository {
    override suspend fun getAll(): List<Structure> = suspendTransaction {
        StructureDAO.all().map { it.toStructure() }
    }

    override suspend fun getFiltered(
        name: String?,
    ): List<Structure> = suspendTransaction {
        val filters = buildList<Op<Boolean>> {
            when {
                !name.isNullOrBlank() -> add(StructuresTable.name like "%$name%")
            }
        }

        StructureDAO
            .find(filters.reduceOrNull { acc, filter -> acc and filter } ?: Op.TRUE)
            .toList()
            .map { it.toStructure() }
    }

    override suspend fun getById(id: String): Structure? = suspendTransaction {
        StructureDAO.findById(id)?.toStructure()
    }

    override suspend fun add(structure: Structure): Unit = suspendTransaction {
        StructureDAO.new(structure.id) {
            name = structure.name
            description = structure.description
        }
    }

    //todo изменение id
    override suspend fun edit(id: String, structure: Structure): Unit = suspendTransaction {
        StructureDAO.findByIdAndUpdate(id) {
            it.name = structure.name
            it.description = structure.description
        }
    }

    override suspend fun remove(id: String): Unit = suspendTransaction {
        StructuresTable.deleteWhere { StructuresTable.id.eq(id) }
    }
}