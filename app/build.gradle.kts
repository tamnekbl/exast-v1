plugins {
    alias(libs.plugins.kotlin.jvm) // Явно применяем плагин Kotlin JVM
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    application // Добавляем плагин application
}

// Добавляем репозитории
repositories {
    mavenCentral()
}

application {
    mainClass.set("com.inrotate.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.logback.classic)
    implementation(libs.dotenv)
    implementation(libs.exposed.core)
    implementation(libs.poi.ooxml)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.poi.ooxml)
}

// Настраиваем toolchain для Kotlin
kotlin {
    jvmToolchain(17)
}

// Задача для запуска приложения
tasks.withType<JavaExec> {
    classpath = sourceSets.main.get().runtimeClasspath
}

// Это заставит Gradle искать тесты JUnit 5
tasks.withType<Test> {
    useJUnitPlatform()
}
