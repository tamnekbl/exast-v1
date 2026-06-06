# exast-v1: структура, архитектура и технологии

`exast-v1` - серверное Kotlin-приложение на Ktor для работы с мероприятиями, организациями, участниками, справочниками и аналитикой посещаемости. Проект разделен на три Gradle-модуля:

- `domain` - доменные модели и интерфейсы репозиториев.
- `data` - слой доступа к PostgreSQL/H2 через Exposed DAO.
- `app` - HTTP API, сериализация, авторизация, импорт Excel, интеграция с внешним AI-сервисом.

## Текстовая структура проекта

```text
exast-v1/
|-- build.gradle.kts                 # корневые Gradle-плагины и общие репозитории
|-- settings.gradle.kts              # подключение модулей app, data, domain
|-- gradle.properties
|-- gradlew
|-- gradlew.bat
|-- README.md
|-- README2.md                       # подробное описание структуры проекта
|-- env.example                      # пример переменных окружения
|-- .env                             # локальные переменные окружения, не должен попадать в репозиторий
|
|-- gradle/
|   |-- libs.versions.toml           # каталог версий библиотек и Gradle-плагинов
|   `-- wrapper/
|       |-- gradle-wrapper.jar
|       `-- gradle-wrapper.properties
|
|-- domain/
|   |-- build.gradle.kts
|   `-- src/main/kotlin/com/inrotate/
|       |-- models/
|       |   |-- Event.kt             # Event, EventFormat, EventLevel, EventType, OrganizationRole
|       |   |-- EventParticipant.kt  # участник мероприятия и роль участия
|       |   |-- Organization.kt      # Organization, OrganizationType
|       |   `-- Participant.kt       # Participant, Speciality, StudyMode
|       `-- repository/
|           `-- Repositories.kt      # CrudRepository и доменные интерфейсы репозиториев
|
|-- data/
|   |-- build.gradle.kts
|   |-- src/main/kotlin/com/inrotate/
|   |   |-- db/
|   |   |   |-- EventDAO.kt          # таблица events, DAO EventDAO, связи с типами/организациями/участниками
|   |   |   |-- EventTypeDAO.kt      # event_types и таблица связи event_event_types
|   |   |   |-- Extensions.kt        # PostgreSQL enum mapping для Exposed
|   |   |   |-- OrganizationDAO.kt   # organizations и organizations_events
|   |   |   |-- OrganizationTypeDAO.kt
|   |   |   |-- ParticipantDAO.kt
|   |   |   |-- ParticipationDAO.kt   # composite key event_id + participant_id
|   |   |   |-- RoleDAO.kt
|   |   |   |-- SpecialityDAO.kt
|   |   |   `-- Transactions.kt      # suspendTransaction на Dispatchers.IO
|   |   `-- repository/
|   |       |-- EventRepositoryImpl.kt
|   |       |-- EventTypeRepositoryImpl.kt
|   |       |-- OrganizationRepositoryImpl.kt
|   |       |-- OrganizationTypeRepositoryImpl.kt
|   |       |-- ParticipantRepositoryImpl.kt
|   |       |-- RoleRepositoryImpl.kt
|   |       `-- SpecialityRepositoryImpl.kt
|   |-- src/main/resources/db/backups/
|   |   |-- exast_db_base_v1.sql
|   |   |-- exast_db_v2.sql
|   |   |-- exast_db_v3.backup
|   |   `-- exast_db_v4.backup
|   `-- src/test/kotlin/com/inrotate/
|       |-- IntegrationTest.kt
|       |-- SimpleTest.kt
|       |-- TestDatabase.kt
|       |-- db/EventDAOTest.kt
|       `-- repository/
|           |-- EventRepositoryImplTest.kt
|           |-- EventTypeRepositoryImplTest.kt
|           |-- OrganizationRepositoryImplTest.kt
|           |-- OrganizationTypeRepositoryImplTest.kt
|           `-- ParticipantRepositoryImplTest.kt
|
`-- app/
    |-- build.gradle.kts
    |-- src/main/kotlin/com/inrotate/
    |   |-- Application.kt           # запуск Netty на 0.0.0.0:8080 и подключение Ktor-модулей
    |   |-- Authentication.kt        # Basic Auth: auth-basic
    |   |-- Databases.kt             # подключение Exposed Database из .env
    |   |-- Routing.kt               # сборка репозиториев, сервисов и всех маршрутов
    |   |-- Serialization.kt         # kotlinx.serialization JSON для Ktor
    |   |
    |   |-- routes/
    |   |   |-- AnalyticsApi.kt       # /api/v1/analytics/*
    |   |   |-- EventsApi.kt          # /api/v1/events/*
    |   |   |-- OrganizationsApi.kt
    |   |   |-- OrganizationTypesApi.kt
    |   |   |-- ParticipantsApi.kt
    |   |   |-- Responses.kt
    |   |   |-- RolesApi.kt
    |   |   `-- SpecialitiesApi.kt
    |   |
    |   |-- models/
    |   |   |-- EventRequest.kt       # входная DTO и преобразование в доменную Event
    |   |   |-- EventResponse.kt      # выходная DTO и toResponse()
    |   |   |-- EventParticipantResponse.kt
    |   |   |-- OrganizationRequest.kt
    |   |   |-- OrganizationResponse.kt
    |   |   |-- OrganizationTypeRequest.kt
    |   |   |-- OrganizationTypeResponse.kt
    |   |   |-- ParticipantReqest.kt   # имя файла содержит опечатку Reqest
    |   |   |-- ParticipantResponse.kt
    |   |   |-- RoleRequest.kt
    |   |   |-- RoleResponse.kt
    |   |   |-- SpecialityRequest.kt
    |   |   |-- SpecialityResponse.kt
    |   |   `-- importer/
    |   |       |-- EventRaw.kt        # промежуточная модель строки Excel
    |   |       |-- UniversalDateParser.kt
    |   |       `-- XlsxParser.kt      # импорт .xlsx через Apache POI
    |   |
    |   |-- services/
    |   |   `-- EventService.kt       # сервис создания события с организациями
    |   |
    |   `-- analytics/
    |       |-- AiAnalyticsService.kt  # бизнес-логика обучения/прогноза/проверки AI
    |       |-- AiServiceConfig.kt     # конфигурация AI-сервиса из .env
    |       |-- AnalyticsException.kt  # доменные ошибки аналитики
    |       |-- dto/
    |       |   `-- AnalyticsDtos.kt
    |       `-- ai/
    |           |-- client/
    |           |   |-- AiServiceClient.kt
    |           |   `-- HttpAiServiceClient.kt
    |           |-- dataset/
    |           |   `-- AiTrainingDatasetBuilder.kt
    |           |-- dto/
    |           |   |-- AiModelDtos.kt
    |           |   |-- AiPredictionDtos.kt
    |           |   `-- AiTrainingDtos.kt
    |           `-- mapper/
    |               `-- AiEventMapper.kt
    |
    |-- src/main/resources/
    |   |-- logback.xml
    |   `-- static/index.html
    `-- src/test/
        |-- kotlin/com/inrotate/
        |   |-- ApplicationTest.kt
        |   |-- TestPolygon.kt
        |   |-- analytics/
        |   |   |-- AiAnalyticsServiceTest.kt
        |   |   |-- AnalyticsTestFixtures.kt
        |   |   `-- ai/
        |   |       |-- client/HttpAiServiceClientTest.kt
        |   |       |-- dataset/AiTrainingDatasetBuilderTest.kt
        |   |       `-- mapper/AiEventMapperTest.kt
        |   `-- models/importer/
        |       |-- EventRawTest.kt
        |       |-- UniversalDateParserTest.kt
        |       |-- XlsxParserTest.kt
        |       `-- XlsxTestFileCreator.kt
        `-- resources/
            |-- 2021.xlsx
            |-- 2022.xlsx
            |-- 2023.xlsx
            |-- 2024.xlsx
            |-- 2025.xlsx
            `-- 2026.xlsx
```

Служебные каталоги `.gradle/`, `.idea/`, `.kotlin/`, `build/` и `*/build/` не являются частью архитектуры приложения: это локальные файлы IDE и результаты сборки.

## Архитектура по модулям

### `domain`

Модуль содержит чистую предметную область без Ktor и Exposed. Здесь определены:

- сущности: `Event`, `Organization`, `Participant`, `Speciality`, `Role`, `EventParticipant`;
- enum-типы: `EventFormat`, `EventLevel`, `EventType`, `OrganizationRole`, `StudyMode`;
- контракты репозиториев: `CrudRepository`, `EventRepository`, `OrganizationRepository`, `ParticipantRepository` и другие.

Ключевой контракт:

```kotlin
interface CrudRepository<ID, T> {
    suspend fun getAll(): List<T>
    suspend fun getById(id: ID): T?
    suspend fun add(entity: T): T
    suspend fun update(entity: T): T
    suspend fun delete(id: ID): Boolean
}
```

`EventRepository` расширяет базовый CRUD фильтрацией и пакетным импортом:

```kotlin
interface EventRepository : CrudRepository<Int, Event> {
    suspend fun getFiltered(title: String?, startDate: String?, endDate: String?): List<Event>
    suspend fun addAll(events: List<Event>): List<Event>
}
```

### `data`

Модуль реализует репозитории из `domain` через Exposed DAO. Основная идея слоя:

```text
PostgreSQL tables <-> Exposed Table/DAO <-> domain model <-> repository interface
```

Важные элементы:

- `EventsTable`, `EventDAO` - таблица и DAO для мероприятий.
- `OrganizationsTable`, `OrganizationDAO` - организации.
- `ParticipantsTable`, `ParticipantDAO` - участники.
- `ParticipationTable`, `ParticipationDAO` - связь участников с мероприятиями и ролями.
- `EventEventTypesTable`, `OrganizationsEventsTable` - many-to-many связи.
- `pgEnum()` и `PGEnum` - поддержка PostgreSQL enum в Exposed.
- `suspendTransaction()` - запуск SQL-операций в корутинах на `Dispatchers.IO`.

Пример DAO-маппинга:

```kotlin
class EventDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventDAO>(EventsTable)

    var title by EventsTable.title
    var startedAt by EventsTable.startedAt
    var endedAt by EventsTable.endedAt
    var types by EventTypeDAO via EventEventTypesTable
    var organizations by OrganizationDAO via OrganizationsEventsTable

    fun toEvent() = Event(
        id = id.value,
        title = title,
        startedAt = startedAt,
        endedAt = endedAt,
        types = types.map { it.toEnum() },
        organizations = organizations.map { it.toOrganization() },
        // остальные поля берутся из EventsTable
    )
}
```

Пример асинхронной транзакции:

```kotlin
suspend fun <T> suspendTransaction(statement: suspend Transaction.() -> T): T =
    suspendedTransactionAsync(Dispatchers.IO) { statement() }.await()
```

### `app`

Модуль поднимает Ktor-сервер, подключает инфраструктуру и открывает REST API. Это внешний слой приложения:

```text
HTTP request
  -> Ktor route
  -> Request DTO
  -> domain model
  -> repository interface
  -> data implementation
  -> database
  -> domain model
  -> Response DTO
  -> HTTP response
```

Точка входа:

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureAuthentication()
    configureRouting()
}
```

`Routing.kt` вручную собирает зависимости:

```kotlin
val eventRepository = EventRepositoryImpl()
val organizationRepository = OrganizationRepositoryImpl()
val aiTrainingDatasetBuilder = AiTrainingDatasetBuilder(eventRepository)
val aiAnalyticsService = AiAnalyticsService(
    config = aiServiceConfig,
    eventRepository = eventRepository,
    organizationRepository = organizationRepository,
    datasetBuilder = aiTrainingDatasetBuilder,
    aiServiceClient = HttpAiServiceClient(aiServiceConfig),
)
```

Затем маршруты закрываются Basic Auth:

```kotlin
authenticate("auth-basic") {
    route("/api/v1") {
        configureEvents(eventRepository)
        configureOrganizations(organizationRepository)
        configureOrganizationTypes(organizationTypeRepository)
        configureSpecialities(specialityRepository)
        configureRoles(roleRepository)
        configureAnalytics(aiAnalyticsService, aiTrainingDatasetBuilder)
    }
}
```

## HTTP API

Все основные API находятся под `/api/v1` и требуют Basic Auth. В текущей реализации учетные данные захардкожены:

```text
username: admin
password: 1
```

Основные группы маршрутов:

```text
GET    /                         # health-like Hello World без авторизации
GET    /static/index.html         # статический файл

/api/v1/events
GET    /events?name=&start=&end=  # список/фильтрация мероприятий
GET    /events/{id}               # мероприятие по id
POST   /events                    # создание мероприятия
PUT    /events/{id}               # обновление мероприятия
DELETE /events/{id}               # удаление мероприятия
POST   /events/import/excel       # импорт мероприятий из Excel multipart/form-data

/api/v1/organizations
GET    /organizations?name=
GET    /organizations/{id}
POST   /organizations
PUT    /organizations/{id}
DELETE /organizations/{id}

/api/v1/organizations/types
GET    /organizations/types
GET    /organizations/types/{id}
POST   /organizations/types
PUT    /organizations/types/{id}
DELETE /organizations/types/{id}

/api/v1/participants
GET    /participants
GET    /participants/{id}
POST   /participants
PUT    /participants/{id}
DELETE /participants/{id}

/api/v1/roles
GET    /roles
GET    /roles/{id}
POST   /roles
PUT    /roles/{id}
DELETE /roles/{id}

/api/v1/specialities
GET    /specialities
GET    /specialities/{id}
POST   /specialities
PUT    /specialities/{id}
DELETE /specialities/{id}

/api/v1/analytics
GET    /analytics/health
GET    /analytics/dataset
POST   /analytics/train
POST   /analytics/events/{eventId}/predict-attendance
POST   /analytics/events/{eventId}/predict-scale
POST   /analytics/predict-attendance
POST   /analytics/predict-scale
GET    /analytics/models/latest
GET    /analytics/models
```

## Основные функциональные участки

### Подключение базы данных

`app/src/main/kotlin/com/inrotate/Databases.kt` читает настройки из `.env` через `dotenv-kotlin`:

```kotlin
val dbUrl = dotenv["DB_URL"] ?: throw IllegalStateException("DB_URL not set")
val dbUser = dotenv["DB_USER"] ?: throw IllegalStateException("DB_USER not set")
val dbPassword = dotenv["DB_PASSWORD"] ?: throw IllegalStateException("DB_PASSWORD not set")

Database.connect(dbUrl, user = dbUser, password = dbPassword)
```

### Сериализация JSON

`Serialization.kt` устанавливает Ktor `ContentNegotiation` с `kotlinx.serialization`:

```kotlin
install(ContentNegotiation) {
    json(
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    )
}
```

Это позволяет принимать JSON с лишними полями и не выводить `null` явно.

### CRUD мероприятий

`EventsApi.kt` принимает HTTP-запросы, парсит DTO и вызывает `EventRepository`:

```kotlin
post {
    val eventRequest = call.receive<EventRequest>()
    val event = eventRequest.toEvent()
    eventRepository.add(event)
    call.respond(HttpStatusCode.OK, ApiResponse("Event added successfully", true))
}
```

DTO преобразуется в доменную модель в `EventRequest.kt`:

```kotlin
fun toEvent(id: Int = 0) = Event(
    id = id,
    title = title,
    createdAt = LocalDateTime.now(),
    startedAt = LocalDateTime.parse(startedAt),
    endedAt = endedAt?.let { LocalDateTime.parse(it) },
    types = types,
    organizations = organizations.map { Organization(it) }
)
```

### Импорт Excel

Импорт находится в `POST /api/v1/events/import/excel`.

Поток обработки:

```text
multipart/form-data
  -> PartData.FileItem
  -> XlsxParser.parseEvents(inputStream)
  -> List<EventRaw>
  -> EventRaw.toEvent()
  -> EventRepository.addAll()
```

`XlsxParser` использует Apache POI `XSSFWorkbook`, `DataFormatter` и ожидает заголовки:

```text
id, title, date, level, types, location, format, organizations,
participantsOther, participantsSpo, participantsVo, participantsForeign,
participantsTotal, organizationRole, description
```

Также в парсере есть словарь алиасов организаций, который нормализует разные варианты названий к одному каноническому имени.

### Аналитика и AI-сервис

Подсистема аналитики вынесена в `app/src/main/kotlin/com/inrotate/analytics`.

Основные классы:

- `AiServiceConfig` - читает `AI_SERVICE_*` из `.env`.
- `AiAnalyticsService` - orchestrator для обучения, прогнозов, health-check и списка моделей.
- `AiTrainingDatasetBuilder` - собирает CSV-датасет из мероприятий.
- `AiEventMapper` - преобразует доменные события и черновики в формат внешнего AI-сервиса.
- `HttpAiServiceClient` - Ktor HTTP client для внешнего Python/AI-сервиса.

Поток обучения:

```text
POST /api/v1/analytics/train
  -> AiAnalyticsService.trainAttendanceModel()
  -> AiTrainingDatasetBuilder.buildCsv()
  -> HttpAiServiceClient.train(csvBytes)
  -> external AI service POST /train
```

Поток прогноза:

```text
POST /api/v1/analytics/events/{eventId}/predict-attendance
  -> загрузка Event из EventRepository
  -> AiEventMapper.fromEvent(event)
  -> HttpAiServiceClient.predict(request)
  -> external AI service POST /predict-attendance
```

`HttpAiServiceClient` обрабатывает таймауты, сетевые ошибки, невалидный JSON и ошибки внешнего сервиса, переводя их в `AnalyticsException`-иерархию. В `AnalyticsApi.kt` эти ошибки конвертируются в HTTP-статусы:

```text
AiServiceDisabledException      -> 503 Service Unavailable
AiServiceUnavailableException   -> 503 Service Unavailable
AiServiceTimeoutException       -> 504 Gateway Timeout
AiServiceBadResponseException   -> 502 Bad Gateway
AiModelNotFoundException        -> 409 Conflict
AiBadRequestException           -> 400 Bad Request
AnalyticsValidationException    -> 400 Bad Request
AnalyticsEntityNotFoundException-> 404 Not Found
```

## Используемые технологии и библиотеки

Версии централизованы в `gradle/libs.versions.toml`.

| Технология | Версия | Где используется | Назначение |
|---|---:|---|---|
| Kotlin JVM | 2.3.0 | все модули | основной язык проекта |
| JVM toolchain | 17 | все модули | целевая версия JVM |
| Gradle Wrapper | 8.14.3 | весь проект | сборка проекта |
| Ktor Server | 3.3.3 | `app` | HTTP-сервер, routing, plugins |
| Ktor Netty | 3.3.3 | `app` | серверный движок |
| Ktor Content Negotiation | 3.3.3 | `app` | JSON negotiation |
| kotlinx.serialization JSON | через Ktor 3.3.3 | `app` | сериализация DTO |
| Ktor Status Pages | 3.3.3 | `app` | обработка исключений |
| Ktor Auth | 3.3.3 | `app` | Basic Authentication |
| Ktor Client CIO | 3.3.3 | `app` | HTTP-клиент для AI-сервиса |
| Exposed Core/JDBC/DAO/Java Time | 0.61.0 | `data`, частично `app` | ORM/SQL DSL и DAO |
| PostgreSQL JDBC Driver | 42.7.7 | `data` | подключение к PostgreSQL |
| H2 Database | 2.3.232 | `data` tests | тестовая/локальная in-memory БД |
| dotenv-kotlin | 6.5.0 | `app` | чтение `.env` |
| Apache POI OOXML | 5.2.5 | `app` | чтение `.xlsx` файлов |
| Logback Classic | 1.4.14 | `app` | логирование |
| JUnit Jupiter | 5.10.0 | `data` tests | тестирование |
| kotlin-test-junit5 | 2.3.0 | `app` tests | тестирование Kotlin-кода |
| Ktor Server Test Host | 3.3.3 | `app` tests | тесты Ktor-приложения |
| Ktor Client Mock | 3.3.3 | `app` tests | тестирование HTTP-клиента AI |

## Конфигурация окружения

Пример находится в `env.example`:

```env
DB_URL=your_database_url
DB_USER=your_database_user
DB_PASSWORD=your_database_password

AI_SERVICE_BASE_URL=http://localhost:8085
AI_SERVICE_CONNECT_TIMEOUT_MILLIS=5000
AI_SERVICE_REQUEST_TIMEOUT_MILLIS=30000
AI_SERVICE_SOCKET_TIMEOUT_MILLIS=30000
AI_SERVICE_ENABLED=true
```

Обязательные переменные для запуска сервера:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

Переменные AI-сервиса нужны, если включено:

```env
AI_SERVICE_ENABLED=true
```

При включенном AI обязательно должен быть задан `AI_SERVICE_BASE_URL`.

## Сборка, запуск и тесты

Основные команды:

```bash
./gradlew build
./gradlew test
./gradlew :app:run
./gradlew :data:test
./gradlew :app:test
```

На Windows можно использовать:

```powershell
.\gradlew.bat build
.\gradlew.bat test
.\gradlew.bat :app:run
```

После запуска сервер слушает:

```text
http://0.0.0.0:8080
```

## Зависимости между модулями

```text
domain
  ^ бизнессущности и интерфейсы
  |
data
  ^ реализации репозиториев через Exposed
  |
app
  HTTP API, DTO, маршруты, авторизация, импорт Excel, AI-интеграция
```

Фактические Gradle-зависимости:

```text
app  -> domain
app  -> data
data -> domain
domain -> внешних production-зависимостей нет
```

Такое разделение позволяет держать доменную модель независимой от Ktor и базы данных, а детали хранения и транспорта выносить во внешние слои.

## Тесты

В проекте есть тесты для:

- DAO и репозиториев в `data/src/test`;
- Ktor-приложения в `app/src/test`;
- Excel-импорта: `EventRawTest`, `UniversalDateParserTest`, `XlsxParserTest`;
- AI-подсистемы: `AiAnalyticsServiceTest`, `HttpAiServiceClientTest`, `AiTrainingDatasetBuilderTest`, `AiEventMapperTest`.

Тестовые Excel-файлы лежат в `app/src/test/resources` за 2021-2026 годы.

## Замечания по текущему состоянию кода

- В `Authentication.kt` логин и пароль заданы прямо в коде. Для production лучше вынести их в `.env` или заменить на полноценную схему аутентификации.
- В `StatusPages` сейчас все необработанные исключения возвращаются как текст `500: $cause`; для внешнего API лучше отдавать структурированный `ApiResponse`.
- В названии файла `ParticipantReqest.kt` есть опечатка; корректнее `ParticipantRequest.kt`.
- В некоторых исходниках русские строки выглядят как mojibake при чтении текущей консолью. Если это не только проблема кодировки терминала, стоит привести файлы к UTF-8.
- `EventService` создан, но основной `EventsApi.kt` сейчас напрямую работает с `EventRepository`.
