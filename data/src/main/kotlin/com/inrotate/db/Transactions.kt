package com.inrotate.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

suspend fun <T> suspendTransaction(statement: suspend Transaction.() -> T): T =
    suspendedTransactionAsync(Dispatchers.IO) { statement() }.await()