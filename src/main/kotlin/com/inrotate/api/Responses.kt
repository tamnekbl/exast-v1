package com.inrotate.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val message: String? = null,
    val result: Boolean
)