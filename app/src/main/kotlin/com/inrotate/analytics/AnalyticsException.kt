package com.inrotate.analytics

open class AnalyticsException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class AiServiceDisabledException : AnalyticsException(
    message = "Сервис интеллектуального анализа отключен",
)

open class AiServiceUnavailableException(
    message: String = "Сервис интеллектуального анализа временно недоступен",
    cause: Throwable? = null,
    val statusCode: Int? = null,
) : AnalyticsException(message, cause)

class AiServiceTimeoutException(
    cause: Throwable? = null,
) : AnalyticsException(
    message = "Сервис интеллектуального анализа временно недоступен",
    cause = cause,
)

class AiServiceBadResponseException(
    cause: Throwable? = null,
) : AnalyticsException(
    message = "Сервис интеллектуального анализа вернул некорректный ответ",
    cause = cause,
)

class AiModelNotFoundException(
    cause: Throwable? = null,
) : AnalyticsException(
    message = "Модель прогнозирования еще не обучена. Сначала запустите обучение модели",
    cause = cause,
)

class AiBadRequestException(
    message: String = "Некорректный запрос к сервису интеллектуального анализа",
    cause: Throwable? = null,
) : AnalyticsException(message, cause)

class AiTrainingFailedException(
    message: String,
    cause: Throwable? = null,
) : AnalyticsException(message, cause)

class AiPredictionFailedException(
    message: String,
    cause: Throwable? = null,
) : AnalyticsException(message, cause)

class AiFeatureInsightsFailedException(
    message: String,
    cause: Throwable? = null,
) : AnalyticsException(message, cause)

class AnalyticsValidationException(
    message: String,
    cause: Throwable? = null,
) : AnalyticsException(message, cause)

class AnalyticsEntityNotFoundException(
    message: String,
) : AnalyticsException(message)
