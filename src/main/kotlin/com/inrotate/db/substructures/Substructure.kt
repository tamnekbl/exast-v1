package com.inrotate.db.substructures

import com.inrotate.db.BaseStructure
import kotlinx.serialization.Serializable

@Serializable
data class Substructure(
    override val id: String,
    override val name: String,
    override val description: String
) : BaseStructure