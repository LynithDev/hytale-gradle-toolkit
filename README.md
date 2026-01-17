# Hytale Gradle Toolkit
Pretty simple Gradle plugin for Hytale modding. 

## Features
- [x] One-click server startup (requires Hytale to be installed locally)
- [x] Manifest generation
- [x] Automatic HytaleServer.jar dependency
- [x] Generate sources for better IDE integration

## Examples
See **[example project](./example)**.

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
