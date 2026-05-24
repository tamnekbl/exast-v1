package com.inrotate.analytics.ai.client

class AiServiceException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
