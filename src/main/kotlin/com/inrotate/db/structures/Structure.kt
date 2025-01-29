package com.inrotate.db.structures

import com.inrotate.db.BaseStructure
import kotlinx.serialization.Serializable

@Serializable
data class Structure(
    override val id: String,
    override val name: String,
    override val description: String
) : BaseStructure