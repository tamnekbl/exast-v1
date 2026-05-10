package com.inrotate.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject

class PGEnum<T : Enum<T>>(enumTypeName: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumTypeName
    }
}

inline fun <reified T : Enum<T>> Table.pgEnum(name: String, sql: String): Column<T> {
    return customEnumeration(
        name,
        sql,
        fromDb = { value -> enumValueOf<T>(value as String) },
        toDb = { PGEnum(sql, it) }
    )
}