package com.inrotate.analytics

import io.github.cdimascio.dotenv.Dotenv

data class AiServiceConfig(
    val baseUrl: String?,
    val connectTimeoutMillis: Long,
    val requestTimeoutMillis: Long,
    val socketTimeoutMillis: Long,
    val enabled: Boolean,
) {
    companion object {
        fun from(dotenv: Dotenv): AiServiceConfig {
            val enabled = dotenv.getBoolean("AI_SERVICE_ENABLED", "ai.service.enabled") ?: false

            return AiServiceConfig(
                baseUrl = dotenv.getString("AI_SERVICE_BASE_URL", "ai.service.baseUrl")
                    ?.trim()
                    ?.trimEnd('/'),
                connectTimeoutMillis = dotenv.getLong(
                    "AI_SERVICE_CONNECT_TIMEOUT_MILLIS",
                    "ai.service.connectTimeoutMillis",
                    5_000,
                ),
                requestTimeoutMillis = dotenv.getLong(
                    "AI_SERVICE_REQUEST_TIMEOUT_MILLIS",
                    "ai.service.requestTimeoutMillis",
                    30_000,
                ),
                socketTimeoutMillis = dotenv.getLong(
                    "AI_SERVICE_SOCKET_TIMEOUT_MILLIS",
                    "ai.service.socketTimeoutMillis",
                    30_000,
                ),
                enabled = enabled,
            ).also { config ->
                if (config.enabled && config.baseUrl.isNullOrBlank()) {
                    throw IllegalStateException("AI_SERVICE_BASE_URL is required when AI_SERVICE_ENABLED=true")
                }
            }
        }

        private fun Dotenv.getString(primaryKey: String, fallbackKey: String): String? =
            this[primaryKey] ?: this[fallbackKey]

        private fun Dotenv.getBoolean(primaryKey: String, fallbackKey: String): Boolean? =
            getString(primaryKey, fallbackKey)?.toBooleanStrictOrNull()

        private fun Dotenv.getLong(primaryKey: String, fallbackKey: String, defaultValue: Long): Long =
            getString(primaryKey, fallbackKey)?.toLongOrNull() ?: defaultValue
    }
}
