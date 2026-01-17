import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
}

group = "dev.lynith"
version = "0.1.0"

base {
    archivesName.set(property("plugin.name").toString())
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())
    implementation(libs.kotlinx.serialization)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
    }
}

gradlePlugin {
    plugins {
        create(property("plugin.name").toString()) {
            id = property("plugin.id").toString()
            implementationClass = property("plugin.class").toString()
            version = project.version
            description = property("plugin.description").toString()
            displayName = property("plugin.displayName").toString()
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/${property("publishing.owner")}/${property("publishing.repo")}")
            credentials {
                username = (findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR") ?: System.getenv("USERNAME")) as String?
                password = (findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN") ?: System.getenv("TOKEN")) as String?
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
