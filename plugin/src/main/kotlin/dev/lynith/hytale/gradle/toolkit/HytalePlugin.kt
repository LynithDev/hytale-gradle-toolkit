package dev.lynith.hytale.gradle.toolkit

import dev.lynith.hytale.gradle.toolkit.ext.HytalePluginExtension
import dev.lynith.hytale.gradle.toolkit.ext.resolveConfig
import dev.lynith.hytale.gradle.toolkit.tasks.GenerateManifestTask
import dev.lynith.hytale.gradle.toolkit.tasks.HytaleServerRunnerTask
import dev.lynith.hytale.gradle.toolkit.utils.Dirs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

class HytalePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        // extension handling
        val ext = project.extensions.create("hytale", HytalePluginExtension::class.java)
        val config = ext.resolveConfig(project)

        // register dependency
        project.dependencies.add("compileOnly", project.provider {
            val dir = Dirs(config.installDir.get())

            project.files(dir.serverJar)
        })

        // register our manifest generation task
        val generateManifestTask = project.tasks.register("generateManifest", GenerateManifestTask::class.java) { task ->
            task.manifestJson.set(config.manifest.get().toJson())
            task.outputFile.set(project.layout.buildDirectory.file("generated/manifest.json"))
        }

        // we hook the manifest generation task to every ProcessResources task
        project.tasks.withType(ProcessResources::class.java)?.configureEach {
            it.dependsOn(generateManifestTask)
            it.from(generateManifestTask)
        }


        project.tasks.register("runServer", HytaleServerRunnerTask::class.java) { task ->
            // set inputs
            val dir = config.installDir.map { Dirs(it) }

            task.runDir.set(config.server.flatMap { it.runDir })
            task.javaBin.set(config.server.flatMap { it.javaBin })

            task.assetsZip.set(
                project.layout.file(
                    dir.map { dir ->
                        dir.assetsZip.toFile()
                    }
                )
            )

            task.serverJar.set(
                project.layout.file(
                    dir.map { dir ->
                        dir.serverJar.toFile()
                    }
                )
            )
        }

    }

}