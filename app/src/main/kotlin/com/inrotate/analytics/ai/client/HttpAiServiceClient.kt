package com.inrotate.analytics.ai.client

import com.inrotate.analytics.AiServiceConfig
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
import java.io.IOException
import java.net.ConnectException

class HttpAiServiceClient(
    private val config: AiServiceConfig,
    private val httpClient: HttpClient = createHttpClient(config),
) : AiServiceClient {
    private val baseUrl: String = requireNotNull(config.baseUrl) {
        "ai.service.baseUrl is required for HttpAiServiceClient"
    }

    override suspend fun health(): Boolean = try {
        val response = httpClient.get("$baseUrl/health")
        response.status.isSuccess()
    } catch (_: TimeoutCancellationException) {
        false
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        false
    }

    override suspend fun train(csvBytes: ByteArray): AiTrainingResponse = callAiService("train model") {
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

        response.requireSuccess("train model").body()
    }

    override suspend fun predict(request: AiPredictionRequest): AiPredictionResponse =
        callAiService("predict attendance") {
            val response = httpClient.post("$baseUrl/predict-attendance") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            response.requireSuccess("predict attendance").body()
        }

    override suspend fun getLatestModel(): AiModelMetadata = callAiService("get latest model") {
        httpClient.get("$baseUrl/models/latest")
            .requireSuccess("get latest model")
            .body()
    }

    override suspend fun getModels(): List<AiModelMetadata> = callAiService("get models") {
        httpClient.get("$baseUrl/models")
            .requireSuccess("get models")
            .body()
    }

    private suspend fun HttpResponse.requireSuccess(operation: String): HttpResponse {
        if (status.isSuccess()) {
            return this
        }

        val responseBody = runCatching { bodyAsText() }.getOrNull()
        val category = when (status.value) {
            in 400..499 -> "AI service rejected request"
            in 500..599 -> "AI service failed"
            else -> "AI service returned unexpected status"
        }

        throw AiServiceException(
            message = "$category during $operation: HTTP ${status.value}${responseBody.asSuffix()}",
            statusCode = status.value,
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
} catch (e: AiServiceException) {
    throw e
} catch (e: TimeoutCancellationException) {
    throw AiServiceException("AI service timeout during $operation", cause = e)
} catch (e: CancellationException) {
    throw e
} catch (e: ConnectException) {
    throw AiServiceException("AI service is unavailable during $operation", cause = e)
} catch (e: SerializationException) {
    throw AiServiceException("AI service returned invalid JSON during $operation", cause = e)
} catch (e: IOException) {
    throw AiServiceException("AI service network error during $operation", cause = e)
} catch (e: IllegalStateException) {
    throw AiServiceException("AI service returned invalid response during $operation", cause = e)
}
