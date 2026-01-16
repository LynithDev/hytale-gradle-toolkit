plugins {
    id("java")
    id("dev.lynith.hytale.gradle.toolkit")
}

group = "dev.lynith"
version = "1.0.0"

repositories {
    mavenCentral()
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

        author("Someone Else")
    }
}