plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
    id("maven-publish")
    id("com.gradle.plugin-publish") version "1.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "com.theo"
version = "0.0.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    implementation("org.openapi.generator:org.openapi.generator.gradle.plugin:5.4.0")
    implementation("de.undercouch.download:de.undercouch.download.gradle.plugin:5.1.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.9.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.powermock:powermock-module-junit4:2.0.9")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.9")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("OpenApiPlugin") {
            id = "com.theo.openApiPlugin"
            displayName = "Plugin to generate openAPI code for client/server"
            description = "A plugin that download and generate openAPI code"
            implementationClass = "com.theo.plugins.openapi.OpenApiPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/theochiu2010/OpenApiGradlePlugin"
    vcsUrl = "https://github.com/theochiu2010/OpenApiGradlePlugin"
    tags = listOf("openApi", "codegen")
}

publishing {
    repositories {
        mavenLocal()
    }
}