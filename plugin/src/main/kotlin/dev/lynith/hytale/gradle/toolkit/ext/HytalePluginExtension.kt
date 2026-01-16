package dev.lynith.hytale.gradle.toolkit.ext

import dev.lynith.hytale.gradle.toolkit.utils.Dirs
import dev.lynith.hytale.gradle.toolkit.utils.defaultPackageDir
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.nio.file.Path
import javax.inject.Inject

abstract class HytalePluginExtension @Inject constructor(val objects: ObjectFactory) {

    val installDir: Property<Path> = objects.property(Path::class.java)
    val server: Property<HytalePluginServerExtension> = objects.property(HytalePluginServerExtension::class.java)
    val manifest: Property<HytalePluginManifestExtension> = objects.property(HytalePluginManifestExtension::class.java)

    fun HytalePluginExtension.server(configure: HytalePluginServerExtension.() -> Unit) {
        val instance = objects.newInstance(HytalePluginServerExtension::class.java)
        server.set(instance)

        instance.configure()
    }

    fun HytalePluginExtension.manifest(configure: HytalePluginManifestExtension.() -> Unit) {
        val instance = objects.newInstance(HytalePluginManifestExtension::class.java)
        manifest.set(instance)

        instance.configure()
    }

}

data class HytalePluginConfig(
    val installDir: Provider<Path>,
    val server: Provider<HytalePluginServerConfig>,
    val manifest: Provider<HytalePluginManifestConfig>,
)

fun HytalePluginExtension.resolveConfig(project: Project): HytalePluginConfig {

    val installDirProvider = installDir.convention(defaultPackageDir)

    val serverProvider = resolveServerConfig(project, installDirProvider.map { Dirs(it) })
    val manifestProvider = resolveManifestConfig(project)

    return HytalePluginConfig(
        installDir = installDirProvider,
        server = serverProvider,
        manifest = manifestProvider,
    )
}