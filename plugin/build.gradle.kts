import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.lynith"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlinx.serialization)
    implementation(libs.vineflower)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

gradlePlugin {
    plugins {
        create(project.name.toString()) {
            id = property("plugin.id").toString()
            implementationClass = property("plugin.class").toString()
            version = project.version
            description = property("plugin.description").toString()
            displayName = property("plugin.displayName").toString()
        }
    }
}

publishing {}
