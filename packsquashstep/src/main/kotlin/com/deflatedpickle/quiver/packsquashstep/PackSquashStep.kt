/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.packsquashstep

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.Slug
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.marvin.exceptions.UnsupportedOperatingSystemException
import com.deflatedpickle.marvin.extensions.Thread
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import com.deflatedpickle.quiver.packexport.api.ExportStepType
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.swing.ProgressMonitor
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialogs

object PackSquashStep : BulkExportStep() {
    private val logger = LogManager.getLogger()

    override fun getSlug(): Slug = Slug(
        "DeflatedPickle",
        "PackSquash",
        Version(1, 0, 0)
    )
    override fun getType(): ExportStepType = ExportStepType.ZIPPER
    override fun getIncompatibleTypes(): Collection<ExportStepType> = listOf(
        ExportStepType.ZIPPER
    )

    override fun processFile(
        file: File,
        progressMonitor: ProgressMonitor
    ) {
        var progress = 0
        progressMonitor.maximum = Files.walk(file.toPath()).count().toInt()

        val system = when (OSUtil.getOS()) {
            OSUtil.OS.WINDOWS -> ""
            OSUtil.OS.LINUX -> "linux"
            OSUtil.OS.MAC -> "macos"
            else -> throw UnsupportedOperatingSystemException(OSUtil.os)
        }

        val settings = ConfigUtil.getSettings<PackSquashStepSettings>("deflatedpickle@pack_squash_step#1.0.0")?.let { settings ->
            val arguments = """
            |resource_pack_directory = "."
            |output_file_path = "${file.parentFile.absolutePath}/${file.nameWithoutExtension}.zip"
            |${File(settings.settings).readText()}
            """.trimMargin()
            // println(arguments)

            this.logger.debug("Starting PackSquash")
            val process = ProcessBuilder(
                "${File(".").canonicalPath}/${settings.location}/${settings.executable}" + if (OSUtil.isWindows()) ".exe" else "-$system",
                "-"
            )
                .directory(file)
                .redirectInput(ProcessBuilder.Redirect.PIPE)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start().apply {
                    try {
                        if (this.isAlive) {
                            for (line in arguments.lines()) {
                                outputStream.write(
                                    "$line${System.lineSeparator()}".toByteArray()
                                )
                            }
                            outputStream.flush()
                            outputStream.close()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        TaskDialogs.showException(e)
                    }
                }
            PackSquashStepPlugin.processList.add(process)

            Thread("${getSlug().name} Feed") {
                val outputStream = BufferedInputStream(
                    process.inputStream
                )

                try {
                    outputStream.bufferedReader().forEachLine {
                        if (progressMonitor.isCanceled) {
                            this.logger.debug("The PackSquash task was cancelled")
                            process.destroyForcibly()
                            return@forEachLine
                        }

                        this.logger.trace(it)
                        progressMonitor.note = it
                        progressMonitor.setProgress(++progress)
                    }
                    PackSquashStepPlugin.processList
                } catch (e: IOException) {
                }
                progressMonitor.close()
            }.start()
            this.logger.debug("Started the PackSquash thread")
        }
    }
}
