import com.google.protobuf.gradle.id

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.qameta.allure") version "2.12.0"
    id("com.google.protobuf") version "0.9.4"
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

    // gRPC version pinned to what TestIgnite 0.3.1 brings at runtime (grpc-api/stub/netty-shaded 1.82.2),
    // so its GrpcClient and our generated stubs share one wire stack.
    implementation("io.grpc:grpc-protobuf:1.82.2")
    implementation("io.grpc:grpc-stub:1.82.2")
    implementation("io.grpc:grpc-netty-shaded:1.82.2")
    // allure-grpc (inside TestIgnite's GrpcClient channel) renders payloads with JsonFormat from here.
    implementation("com.google.protobuf:protobuf-java-util:3.25.8")
    // Compile-scope RestAssured for the REST API client — pinned to the rest-assured 6.0.1
    // that TestIgnite ships at runtime.
    implementation("io.rest-assured:rest-assured:6.0.1")

    implementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("io.qameta.allure:allure-junit5:2.29.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(23)
}

// TestIgnite's DtoClassGenerator: generates Kotlin data classes from the DB tables listed in
// dto_generation_config.yml before main compiles. The connector object it reflects into
// (io.testignite.database.connectors.TmsDB) lives in its own source set — putting it in main
// would be circular: the generator needs it compiled, but main can't compile until the
// generated classes exist. Main depends on dtoGen's output instead (no cycle that way), so the
// same TmsDB object is also the runtime connector — no duplicated container bootstrap.
val dtoGen: SourceSet by sourceSets.creating

dependencies {
    "dtoGenImplementation"("io.github.konstantin-se:TestIgnite:0.3.1")
    "dtoGenImplementation"("org.postgresql:postgresql:42.7.4")
    "dtoGenImplementation"("org.testcontainers:postgresql:1.21.4")
    implementation(dtoGen.output)
}

val generateDtoClasses by tasks.registering(JavaExec::class) {
    group = "build"
    description = "Generates table DTOs (io.testignite.tables.*) from the schema of a throwaway Postgres."
    mainClass.set("io.testignite.database.objectGenerator.DtoClassGeneratorKt")
    // files(...) puts db/init-tasks.sql on the classpath for the generation-time Postgres.
    classpath = dtoGen.runtimeClasspath + files("src/main/resources")
    // The Gradle daemon may run on an older JDK; TestIgnite's generator needs the project's 23.
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(23)) })
    inputs.files("src/main/resources/dto_generation_config.yml", "src/main/resources/db/init-tasks.sql")
    outputs.dir(layout.buildDirectory.dir("generated/sources/dto/main/kotlin"))
}

sourceSets.main {
    kotlin.srcDir(generateDtoClasses)
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.8" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.82.2" }
    }
    generateProtoTasks {
        all().forEach { task -> task.plugins { id("grpc") } }
    }
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
