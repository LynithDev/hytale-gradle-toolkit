package dev.lynith.hytale.gradle.toolkit

import dev.lynith.hytale.gradle.toolkit.ext.HytalePluginExtension
import dev.lynith.hytale.gradle.toolkit.ext.resolveConfig
import dev.lynith.hytale.gradle.toolkit.tasks.GenManifestTask
import dev.lynith.hytale.gradle.toolkit.tasks.GenSourcesTask
import dev.lynith.hytale.gradle.toolkit.tasks.HytaleServerRunnerTask
import dev.lynith.hytale.gradle.toolkit.utils.Dirs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.language.jvm.tasks.ProcessResources

class HytalePlugin : Plugin<Project> {

    override fun apply(project: Project) {

        // --- EXTENSION ---

        val ext = project.extensions.create("hytale", HytalePluginExtension::class.java)
        val config = ext.resolveConfig(project)
        val dir = config.installDir.map { Dirs(it) }

        // --- TASKS ---

        // register our manifest generation task
        project.tasks.register("genManifest", GenManifestTask::class.java) { task ->
            task.manifestJson.set(config.manifest.get().toJson())
            task.outputFile.set(project.layout.buildDirectory.file("generated/manifest.json"))
        }

        // register genSources task
        val genSourcesTask = project.tasks.register("genSources", GenSourcesTask::class.java) { task ->
            val serverJar = dir.get().serverJar.toFile()

            task.serverJar.set(project.file(serverJar))
            task.outputArchive.set(project.layout.buildDirectory.file("generated/${serverJar.nameWithoutExtension}-sources.jar"))
        }

        // register runServer task
        project.tasks.register("runServer", HytaleServerRunnerTask::class.java) { task ->
            // set inputs
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

            task.jvmArgs.set(config.server.flatMap { it.jvmArgs })
            task.serverArgs.set(config.server.flatMap { it.serverArgs })
        }

        // --- DEPENDENCIES ---

        // add dependencies to the target project
        project.dependencies.add("compileOnly", project.files(
            // HytaleServer.jar
            dir.get().serverJar,

            // generated sources
            genSourcesTask.flatMap { it.outputArchive }
        ))
    }

}