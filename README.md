# Hytale Gradle Toolkit
Pretty simple Gradle plugin for Hytale modding

## Usage
`build.gradle.kts`
```kotlin
plugins {
    id("dev.lynith.hytale.gradle.toolkit")
}

hytale {
    manifest {
        mainClass = "dev.lynith.example.ExamplePlugin"
        name = "Example"

        author {
            name = "Lynith"
            email = "me@lynith.dev"
            website = "https://lynith.dev/"
        }
    }
}
```