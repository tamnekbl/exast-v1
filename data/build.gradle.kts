// Файл: data/build.gradle.kts
plugins {
    alias(libs.plugins.kotlin.jvm)
}

// Добавляем репозитории
repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.java.time)
    // JDBC driver
    implementation(libs.postgresql)
    implementation(libs.h2)

    testImplementation(kotlin("test"))
    //testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}