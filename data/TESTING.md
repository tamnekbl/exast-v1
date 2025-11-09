# Тестирование модуля Data

## Структура тестов

Тесты для модуля Data организованы следующим образом:

```
data/src/test/
├── kotlin/
│   └── com/inrotate/
│       ├── TestDatabase.kt          # Базовый класс для тестов с БД
│       ├── SimpleTest.kt            # Простой тест для проверки конфигурации
│       ├── IntegrationTest.kt       # Тесты для проверки связанны операций
│       ├── db/
│       │   └── EventDAOTest.kt      # Тесты для DAO слоя
│       └── repository/
│           ├── EventRepositoryImplTest.kt          # Тесты для EventRepository
│           ├── EventTypeRepositoryImplTest.kt      # Тесты для EventTypeRepository
│           ├── OrganizationRepositoryImplTest.kt    # Тесты для OrganizationRepository
│           ├── OrganizationTypeRepositoryImplTest.kt # Тесты для OrganizationTypeRepository
│           └── ParticipantRepositoryImplTest.kt    # Тесты для ParticipantRepository
└── resources/
    └── junit-platform.properties    # Конфигурация JUnit
```

## Запуск тестов

### Из командной строки (Windows)

```cmd
cd e:\kotlin\back\exast-v1
gradlew.bat :data:test
```

### Из командной строки (Linux/macOS)

```bash
cd e:\kotlin\back\exast-v1
./gradlew :data:test
```

### Запуск конкретного теста

```cmd
gradlew.bat :data:test --tests "com.inrotate.repository.EventRepositoryImplTest"
```

### Запуск тестов с подробным выводом

```cmd
gradlew.bat :data:test --info
```

## Конфигурация тестов

Тесты используют in-memory базу данных H2 для изоляции тестов и ускорения выполнения.

## Покрытие тестами

Тесты покрывают следующие аспекты:

1. Создание сущностей
2. Чтение сущностей
3. Обновление сущностей
4. Удаление сущностей
5. Получение списков сущностей
6. Преобразование между DAO и моделями домена
7. Фильтрация данных
8. Работа с отношениями между сущностями

## Добавление новых тестов

1. Создайте новый файл теста в соответствующем каталоге
2. Унаследуйте класс от `TestDatabase`
3. Добавьте необходимые аннотации `@Test`
4. Используйте `runBlocking` для тестирования suspend функций