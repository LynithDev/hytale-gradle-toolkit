package dev.lynith.hytale.gradle.toolkit.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.java.decompiler.api.Decompiler
import org.jetbrains.java.decompiler.main.decompiler.SingleFileSaver
import org.jetbrains.java.decompiler.main.extern.IFernflowerLogger
import org.jetbrains.java.decompiler.main.extern.IFernflowerPreferences
import org.slf4j.LoggerFactory

abstract class GenSourcesTask : DefaultTask() {

    init {
        group = "hytale"
        description = "Decompiles the server jar and applies the sources to the project. Useful for IDE's such as Intellij, as it allows for indexing"

        doLast {
            // should I do the publishing stuff here?
        }
    }

    @get:InputFile
    abstract val serverJar: RegularFileProperty

    @get:OutputFile
    abstract val outputArchive: RegularFileProperty

    @TaskAction
    fun decomp() {
        logger.lifecycle("Decompiling '${serverJar.get().asFile.name}'")
        val decompiler = Decompiler.builder()
            .inputs(serverJar.get().asFile)
            .allowedPrefixes("com/hypixel/")
            .logger(FernflowerLogger(logger))
            .option(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, true)
            .option(IFernflowerPreferences.HIDE_DEFAULT_CONSTRUCTOR, false)
            .option(IFernflowerPreferences.REMOVE_BRIDGE, false)
            .option(IFernflowerPreferences.ASCII_STRING_CHARACTERS, true)
            .option(IFernflowerPreferences.USE_DEBUG_VAR_NAMES, true)
            .option(IFernflowerPreferences.LOG_LEVEL, getLogLevel())
            .output(SingleFileSaver(outputArchive.get().asFile))
            .build()

        decompiler.decompile()
    }

    private fun getLogLevel(): String {
        var logLevel = "info"

        if (logger.isWarnEnabled)
            logLevel = "warning"
        if (logger.isErrorEnabled)
            logLevel = "error"
        if (logger.isDebugEnabled || logger.isTraceEnabled)
            logLevel = "debug"

        return logLevel
    }

    companion object {
        private class FernflowerLogger(val logger: Logger) : IFernflowerLogger() {
            override fun writeMessage(
                message: String?,
                severity: Severity?
            ) {
                writeMessage(message, severity, null)
            }

            override fun writeMessage(
                message: String?,
                severity: Severity?,
                t: Throwable?
            ) {
                val logLevel = when (severity) {
                    Severity.INFO -> LogLevel.LIFECYCLE
                    Severity.WARN -> LogLevel.WARN
                    Severity.ERROR -> LogLevel.ERROR
                    Severity.TRACE -> LogLevel.DEBUG
                    null -> LogLevel.LIFECYCLE
                }

                if (t == null) {
                    logger.log(logLevel, message)
                } else {
                    logger.log(logLevel, message, t)
                }
            }

        }
    }

}