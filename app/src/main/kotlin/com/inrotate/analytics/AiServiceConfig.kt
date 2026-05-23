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
            val enabled = dotenv.getBoolean("ai.service.enabled", "AI_SERVICE_ENABLED") ?: false

            return AiServiceConfig(
                baseUrl = dotenv.getString("ai.service.baseUrl", "AI_SERVICE_BASE_URL")
                    ?.trim()
                    ?.trimEnd('/'),
                connectTimeoutMillis = dotenv.getLong(
                    "ai.service.connectTimeoutMillis",
                    "AI_SERVICE_CONNECT_TIMEOUT_MILLIS",
                    5_000,
                ),
                requestTimeoutMillis = dotenv.getLong(
                    "ai.service.requestTimeoutMillis",
                    "AI_SERVICE_REQUEST_TIMEOUT_MILLIS",
                    30_000,
                ),
                socketTimeoutMillis = dotenv.getLong(
                    "ai.service.socketTimeoutMillis",
                    "AI_SERVICE_SOCKET_TIMEOUT_MILLIS",
                    30_000,
                ),
                enabled = enabled,
            ).also { config ->
                if (config.enabled && config.baseUrl.isNullOrBlank()) {
                    throw IllegalStateException("ai.service.baseUrl is required when ai.service.enabled=true")
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
