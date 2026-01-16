package dev.lynith.hytale.gradle.toolkit.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

abstract class GenerateManifestTask @Inject constructor() : DefaultTask() {

    init {
        group = "hytale"
        description = "Generates manifest file"
    }

    @get:Input
    abstract val manifestJson: Property<String>

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val json = manifestJson.get()
        val out = outputFile.get().asFile

        out.parentFile.mkdirs()
        out.writeText(json)
    }

}