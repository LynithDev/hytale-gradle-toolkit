# Hytale Gradle Toolkit
Pretty simple Gradle plugin for Hytale modding. 

## Features
- [x] One-click server startup (requires Hytale to be installed locally)
- [x] Manifest generation
- [x] Automatic HytaleServer.jar dependency
- [x] Generate sources for better IDE integration

## Usage
`build.gradle.kts`
```kotlin
plugins {
    id("dev.lynith.hytale-gradle-toolkit") version "v0.1.1"
}

hytale {
    manifest {
        mainClass = "dev.lynith.example.ExamplePlugin"
        name = "Example"
        group = "Lynith"

        author {
            name = "Lynith"
            email = "me@lynith.dev"
            website = "https://lynith.dev/"
        }
    }

    server {
        jvmArgs.add("-Xmx4G")
    }
}
```

## Installation
Artifacts are built and hosted on [JitPack](https://jitpack.io/).

<details>

<summary>Gradle (Kotlin)</summary>

`settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io")
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dev.lynith.hytale-gradle-toolkit") {
                useModule("com.github.LynithDev:hytale-gradle-toolkit:${requested.version}")
            }
        }
    }
}
```

`build.gradle.kts`
```kotlin
plugins {
    id("dev.lynith.hytale-gradle-toolkit") version "v0.1.1"
}

hytale {
    manifest {
        mainClass = "com.example.MyPlugin"
    }
}
```

</details>