plugins {
    kotlin("jvm") version "2.1.20"
    id("io.qameta.allure") version "2.12.0"
}

group = "com.tms"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.microsoft.playwright:playwright:1.47.0")

    implementation("io.qameta.allure:allure-java-commons:2.29.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    implementation("io.github.konstantin-se:TestIgnite:0.3.1")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.testcontainers:postgresql:1.21.4")

    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.qameta.allure:allure-junit5:2.29.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(23)
}

allure {
    version.set("2.29.0")
    adapter {
        autoconfigure.set(false)
        aspectjWeaver.set(false)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.register<JavaExec>("installPlaywright") {
    group = "verification"
    description = "Installs the Chromium build (plus OS deps) that Playwright tests need — used by CI."
    mainClass.set("com.microsoft.playwright.CLI")
    classpath = sourceSets["main"].runtimeClasspath
    args("install", "--with-deps", "chromium")
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    description = "Serves the mock TMS webapp standalone so you can open it in a real browser."
    mainClass.set("com.tms.app.RunAppKt")
    classpath = sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
}
