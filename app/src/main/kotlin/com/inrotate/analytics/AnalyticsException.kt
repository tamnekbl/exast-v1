package com.inrotate.analytics

class AnalyticsException(
    message: String,
    val code: Code,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    enum class Code {
        AI_DISABLED,
        AI_UNAVAILABLE,
        EVENT_NOT_FOUND,
        ORGANIZATION_NOT_FOUND,
        INVALID_REQUEST,
    }
}
