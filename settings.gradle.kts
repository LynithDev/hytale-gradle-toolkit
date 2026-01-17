rootProject.name = "hytale-gradle-toolkit"

if (System.getenv("JITPACK") == null || System.getenv("CI") == null) {
    include(":example")
}

includeBuild("plugin")

