package dev.lynith.hytale.gradle.toolkit.ext

import dev.lynith.hytale.gradle.toolkit.utils.Dirs
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import javax.inject.Inject

class HytalePluginServerExtension @Inject constructor(objects: ObjectFactory) {

    val runDir: Property<Path> = objects.property(Path::class.java)
    val javaBin: Property<Path> = objects.property(Path::class.java)

}

data class HytalePluginServerConfig(
    val runDir: Provider<Directory>,
    val javaBin: Provider<RegularFile>,
)

fun HytalePluginExtension.resolveServerConfig(project: Project, dirs: Provider<Dirs>): Provider<HytalePluginServerConfig> {
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
            })
        )
    }.orElse(
        HytalePluginServerConfig(
            runDir = project.layout.buildDirectory.dir("run"),
            javaBin = project.layout.file(project.provider {
                val dirs = dirs.get()

                dirs.jreBin.toFile()
            })
        )
    )
}