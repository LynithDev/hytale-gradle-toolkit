package dev.lynith.hytale.gradle.toolkit.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class HytaleServerRunnerTask @Inject constructor() : DefaultTask() {

    @get:InputFile
    abstract val javaBin: RegularFileProperty

    @get:OutputDirectory
    abstract val runDir: DirectoryProperty

    @get:InputFile
    abstract val serverJar: RegularFileProperty

    @get:InputFile
    abstract val assetsZip: RegularFileProperty

    @get:InputFile
    abstract val pluginJar: RegularFileProperty

    @get:Input
    abstract val jvmArgs: ListProperty<String>

    @get:Input
    abstract val serverArgs: ListProperty<String>

    init {
        group = "hytale"
        description = "Run's the Hytale server with your plugin"

        // depend on the jar task
        val jarTask = project.tasks.withType(Jar::class.java).last()
        dependsOn(jarTask)

        // get the output of the jar task
        pluginJar.set(jarTask.archiveFile)
    }

    @get:Inject
    abstract val processer: ExecOperations

    @TaskAction
    fun run() {
        val runDir = runDir.get().asFile

        // copy built plugin into server's mods folder
        val pluginJar = pluginJar.get().asFile
        val modsDir = runDir.resolve("mods")
        if (!modsDir.exists())
            modsDir.mkdirs()

        pluginJar.copyTo(modsDir.resolve(pluginJar.name), overwrite = true)

        // run the server
        processer.exec { process ->
            process.workingDir = runDir

            process.executable = javaBin.get().asFile.absolutePath

            val args = mutableListOf<String>()
            val jvmArgs = mutableListOf<String>().apply { addAll(jvmArgs.get()) }
            val serverArgs = mutableListOf<String>().apply { addAll(serverArgs.get()) }

            if (jvmArgs.isEmpty()) {
                jvmArgs += listOf(
                    "-Xms4G",
                    "-Xmx4G",
                    "-XX:+UseG1GC",
                    "-XX:+ParallelRefProcEnabled",
                )
            }

            args.addAll(jvmArgs)

            // server jar
            args.add("-jar")
            args.add(serverJar.get().asFile.absolutePath)

            // assets zip
            args.add("--assets")
            args.add(assetsZip.get().asFile.absolutePath)

            // server args
            if (serverArgs.isEmpty()) {
                serverArgs += listOf(
                    "--allow-op",
                    "--accept-early-plugins",
//                    "--auth-mode",
//                    "insecure", // todo: replace this with insecure and remove --singleplayer arg
//                    "--singleplayer",
                    "--bind",
                    "0.0.0.0:5520"
                )
            }
            args.addAll(serverArgs)

            // lets not spam their sentry :D
            args.add("--disable-sentry")

            process.args = args

            println("Starting server with the following command:")
            println("${process.executable} ${process.args.joinToString(" ")}")

            process.standardInput = System.`in`
        }
    }

}