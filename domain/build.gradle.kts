// Файл: domain/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Добавляем репозитории
repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}