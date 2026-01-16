package dev.lynith.hytale.gradle.toolkit.utils

import org.gradle.internal.os.OperatingSystem
import java.nio.file.Files
import java.nio.file.Path

class Dirs(packageDir: Path) {
    /**
     * Returns the path to the folder containing the game's client, server and assets folder
     *
     * `.../Hytale/install/release/package/game/latest/`
     */
    val installDir: Path = packageDir.resolve("game/latest")

    /**
     * Returns the path to the server jar
     *
     * `.../Hytale/install/release/package/game/latest/Server/HytaleServer.jar`
     */
    val serverJar: Path = installDir.resolve("Server/HytaleServer.jar")

    /**
     * Returns the path to the assets zip
     *
     * `.../Hytale/install/release/package/game/latest/Assets.zip`
     */
    val assetsZip: Path = installDir.resolve("Assets.zip")

    /**
     * Returns the path to the jre binary
     *
     * `.../Hytale/install/release/package/jre/latest/`
     */
    val jreBin: Path = packageDir.resolve("jre/latest/bin/java")
}

/**
 * Returns the path to the game's package folder
 *
 * `.../Hytale/install/release/package/`
 */
val defaultPackageDir: Path? = when (OperatingSystem.current()) {
    OperatingSystem.WINDOWS -> {
        System.getenv("APPDATA")?.let { appData ->
            Path.of(appData)
                .resolve("Hytale/install/release/package")
                .takeIf(Files::isDirectory)
        }
    }

    OperatingSystem.MAC_OS -> {
        System.getProperty("user.home")?.let { home ->
            Path.of(home)
                .resolve("Library/Application Support/Hytale/install/release/package")
                .takeIf(Files::isDirectory)
        }
    }

    OperatingSystem.LINUX -> {
        listOfNotNull(
            // Flatpak
            System.getProperty("user.home")?.let {
                Path.of(it)
                    .resolve(".var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package")
            },
            // XDG
            System.getenv("XDG_DATA_HOME")?.let { Path.of(it).resolve("Hytale/install/release/package") },
            // Fallback
            System.getProperty("user.home")
                ?.let { Path.of(it).resolve(".local/share/Hytale/install/release/package") }
        ).firstOrNull(Files::isDirectory)
    }

    else -> null
}