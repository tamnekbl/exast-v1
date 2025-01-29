package com.inrotate.repository


import com.inrotate.db.substructures.Substructure
import com.inrotate.db.substructures.SubstructureDAO
import com.inrotate.db.substructures.SubstructuresTable
import com.inrotate.db.suspendTransaction
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere

class SubstructureQueries : SubstructureRepository {
    override suspend fun getAll(): List<Substructure> = suspendTransaction {
        SubstructureDAO.all().map { it.toSubstructure() }
    }

    override suspend fun getFiltered(
        name: String?,
    ): List<Substructure> = suspendTransaction {
        val filters = buildList<Op<Boolean>> {
            when {
                !name.isNullOrBlank() -> add(SubstructuresTable.name like "%$name%")
            }
        }

        SubstructureDAO
            .find(filters.reduceOrNull { acc, filter -> acc and filter } ?: Op.TRUE)
            .toList()
            .map { it.toSubstructure() }
    }

    override suspend fun getById(id: String): Substructure? = suspendTransaction {
        SubstructureDAO.findById(id)?.toSubstructure()
    }

    override suspend fun add(structure: Substructure): Unit = suspendTransaction {
        //todo проверка на валидность времён. время начала не позже времени конца
        SubstructureDAO.new(structure.id) {
            name = structure.name
            description = structure.description
        }
    }

    override suspend fun edit(id: String, structure: Substructure): Unit = suspendTransaction {
        SubstructureDAO.findByIdAndUpdate(id) {
            it.name = structure.name
            it.description = structure.description
        }
    }

    override suspend fun remove(id: String): Unit = suspendTransaction {
        SubstructuresTable.deleteWhere { SubstructuresTable.id.eq(id) }
    }
}