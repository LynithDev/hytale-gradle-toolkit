# Hytale Gradle Toolkit
Pretty simple Gradle plugin for Hytale modding. 

Lets you setup and startup a local server with 1 click of a button, thanks to the `:hytale:runServer` task.

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
