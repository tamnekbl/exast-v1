package com.inrotate.analytics.ai.client

import com.inrotate.analytics.*
import com.inrotate.analytics.ai.dto.AiModelMetadata
import com.inrotate.analytics.ai.dto.AiPredictionRequest
import com.inrotate.analytics.ai.dto.AiPredictionResponse
import com.inrotate.analytics.ai.dto.AiTrainingResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.ConnectException

private val aiClientLogger = LoggerFactory.getLogger(HttpAiServiceClient::class.java)

class HttpAiServiceClient(
    private val config: AiServiceConfig,
    private val httpClient: HttpClient = createHttpClient(config),
) : AiServiceClient {
    private val baseUrl: String
        get() = requireNotNull(config.baseUrl) {
            "ai.service.baseUrl is required for HttpAiServiceClient"
        }

    override suspend fun health(): Boolean = try {
        val response = httpClient.get("$baseUrl/health")
        response.status.isSuccess()
    } catch (e: TimeoutCancellationException) {
        aiClientLogger.warn("AI service health check timed out", e)
        false
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        aiClientLogger.warn("AI service health check failed", e)
        false
    }

    override suspend fun train(csvBytes: ByteArray): AiTrainingResponse = callAiService("train model") {
        aiClientLogger.info("Sending AI training dataset to Python service: bytes={}", csvBytes.size)
        val response = httpClient.post("$baseUrl/train") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            "file",
                            csvBytes,
                            Headers.build {
                                append(HttpHeaders.ContentType, ContentType.Text.CSV.toString())
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"file\"; filename=\"dataset.csv\"",
                                )
                            },
                        )
                    },
                ),
            )
        }

        val result = response.requireSuccess("train model").body<AiTrainingResponse>()
        aiClientLogger.info("AI training response received: modelVersion={}", result.modelVersion)
        result
    }

    override suspend fun predict(request: AiPredictionRequest): AiPredictionResponse =
        callAiService("predict attendance") {
            aiClientLogger.info("Sending AI attendance prediction request to Python service")
            val response = httpClient.post("$baseUrl/predict-attendance") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val result = response.requireSuccess("predict attendance").body<AiPredictionResponse>()
            aiClientLogger.info("AI prediction response received: modelVersion={}", result.modelVersion)
            result
        }

    override suspend fun getLatestModel(): AiModelMetadata = callAiService("get latest model") {
        val result = httpClient.get("$baseUrl/models/latest")
            .requireSuccess("get latest model")
            .body<AiModelMetadata>()
        aiClientLogger.info("AI latest model response received: modelVersion={}", result.modelVersion)
        result
    }

    override suspend fun getModels(): List<AiModelMetadata> = callAiService("get models") {
        val result = httpClient.get("$baseUrl/models")
            .requireSuccess("get models")
            .body<List<AiModelMetadata>>()
        aiClientLogger.info("AI models response received: count={}", result.size)
        result
    }

    private suspend fun HttpResponse.requireSuccess(operation: String): HttpResponse {
        if (status.isSuccess()) {
            return this
        }

        val responseBody = runCatching { bodyAsText() }.getOrNull()
        aiClientLogger.warn(
            "AI service returned non-success status: operation={}, statusCode={}, responseSnippet={}",
            operation,
            status.value,
            responseBody.safeLogSnippet(),
        )
        if (status.value == HttpStatusCode.UnprocessableEntity.value || status.value == HttpStatusCode.BadRequest.value) {
            throw AiServiceBadResponseException(
                IllegalStateException(
                    "AI service rejected request during $operation: HTTP ${status.value}${responseBody.asSuffix()}",
                ),
            )
        }

        throw AiServiceUnavailableException(
            message = "Сервис интеллектуального анализа временно недоступен",
            statusCode = status.value,
            cause = IllegalStateException(
                "AI service returned HTTP ${status.value} during $operation${responseBody.asSuffix()}",
            ),
        )
    }

    private fun HttpStatusCode.isSuccess(): Boolean = value in 200..299

    private fun String?.asSuffix(): String =
        if (isNullOrBlank()) "" else ", body: $this"

    companion object {
        fun createHttpClient(config: AiServiceConfig): HttpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }

            install(HttpTimeout) {
                connectTimeoutMillis = config.connectTimeoutMillis
                requestTimeoutMillis = config.requestTimeoutMillis
                socketTimeoutMillis = config.socketTimeoutMillis
            }
        }
    }
}

private suspend fun <T> callAiService(operation: String, block: suspend () -> T): T = try {
    block()
} catch (e: AnalyticsException) {
    throw e
} catch (e: TimeoutCancellationException) {
    aiClientLogger.warn("AI service timeout during {}", operation, e)
    throw AiServiceTimeoutException(e)
} catch (e: CancellationException) {
    throw e
} catch (e: ConnectException) {
    aiClientLogger.warn("AI service unavailable during {}", operation, e)
    throw AiServiceUnavailableException(cause = e)
} catch (e: SerializationException) {
    aiClientLogger.warn("AI service returned invalid JSON during {}", operation, e)
    throw AiServiceBadResponseException(e)
} catch (e: IOException) {
    aiClientLogger.warn("AI service network error during {}", operation, e)
    throw AiServiceUnavailableException(cause = e)
} catch (e: IllegalStateException) {
    aiClientLogger.warn("AI service returned invalid response during {}", operation, e)
    throw AiServiceBadResponseException(e)
}

private fun String?.safeLogSnippet(maxLength: Int = 500): String =
    when {
        isNullOrBlank() -> ""
        length <= maxLength -> replace("\r", "\\r").replace("\n", "\\n")
        else -> take(maxLength).replace("\r", "\\r").replace("\n", "\\n") + "...(truncated)"
    }
