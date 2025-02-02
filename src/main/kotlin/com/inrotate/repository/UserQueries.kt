package com.inrotate.repository

import com.inrotate.db.suspendTransaction
import com.inrotate.db.users.User
import com.inrotate.db.users.UsersDAO
import com.inrotate.db.users.UsersTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


class UserQueries : UserRepository {
    override suspend fun getAll(): List<User> = suspendTransaction {
        UsersDAO.all().map { it.toUser() }
    }

    override suspend fun getById(id: String): User? = suspendTransaction {
        UsersDAO.findById(id)?.toUser()
    }

    override suspend fun getFiltered(name: String?): List<User> = suspendTransaction {
        val filters = buildList<Op<Boolean>> {
            when {
                !name.isNullOrBlank() -> add(UsersTable.name like "%$name%")
            }
        }

        UsersDAO
            .find(filters.reduceOrNull { acc, filter -> acc and filter } ?: Op.TRUE)
            .toList()
            .map { it.toUser() }
    }

    override suspend fun add(user: User): Unit = suspendTransaction {
        UsersDAO.new(user.id) {
            name = user.name
            email = user.email
        }
    }

    override suspend fun edit(id: String, user: User): Unit = suspendTransaction {
        UsersDAO.findByIdAndUpdate(id) {
            it.name = user.name
            it.email = user.email
        }
    }

    override suspend fun remove(id: String): Unit = suspendTransaction {
        UsersTable.deleteWhere { UsersTable.id.eq(id) }
    }

}