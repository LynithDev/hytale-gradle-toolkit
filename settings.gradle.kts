rootProject.name = "hytale-gradle-toolkit"

if (System.getenv("JITPACK") == null) {
    include(":example")
}

includeBuild("plugin")

