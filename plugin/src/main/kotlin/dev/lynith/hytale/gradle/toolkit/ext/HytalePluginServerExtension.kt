package dev.lynith.hytale.gradle.toolkit.ext

import dev.lynith.hytale.gradle.toolkit.utils.Dirs
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import javax.inject.Inject

abstract class HytalePluginServerExtension @Inject constructor(objects: ObjectFactory) {

    val runDir: Property<Path> = objects.property(Path::class.java)
    val javaBin: Property<Path> = objects.property(Path::class.java)
    val jvmArgs: ListProperty<String> = objects.listProperty(String::class.java)
    val serverArgs: ListProperty<String> = objects.listProperty(String::class.java)

}

data class HytalePluginServerConfig(
    val runDir: Provider<Directory>,
    val javaBin: Provider<RegularFile>,
    val jvmArgs: Provider<MutableList<String>>,
    val serverArgs: Provider<MutableList<String>>,
)

fun HytalePluginExtension.resolveServerConfig(project: Project, dirs: Provider<Dirs>): Provider<HytalePluginServerConfig> {
    val defaultServerArgs = mutableListOf(
        "--allow-op",
        "--accept-early-plugins",
        "--bind",
        "0.0.0.0:5520"
    )

    val defaultJvmArgs = mutableListOf(
        "-Xms4G",
        "-Xmx4G",
        "-XX:+UseG1GC",
        "-XX:+ParallelRefProcEnabled",
    )

    return server.map { ext ->
        HytalePluginServerConfig(
            runDir = project.layout.dir(project.provider {
                ext.runDir.getOrElse(
                    project.layout.buildDirectory.dir("run").get().asFile.toPath()
                ).toFile()
            }),

            javaBin = project.layout.file(project.provider {
                val dirs = dirs.get()

                ext.javaBin.convention(dirs.jreBin).get().toFile()
            }),

            jvmArgs = ext.jvmArgs.convention(defaultJvmArgs).map { it.toMutableList() },
            serverArgs = ext.serverArgs.convention(defaultServerArgs).map { it.toMutableList() },
        )
    }.orElse(
        HytalePluginServerConfig(
            runDir = project.layout.buildDirectory.dir("run"),
            javaBin = project.layout.file(project.provider {
                dirs.get().jreBin.toFile()
            }),
            jvmArgs = project.provider { defaultJvmArgs },
            serverArgs = project.provider { defaultServerArgs },
        )
    )
}