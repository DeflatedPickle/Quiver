package com.deflatedpickle.quiver.packsquashstep

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.extensions.Thread
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import com.deflatedpickle.quiver.packexport.api.ExportStepType
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.swing.JProgressBar
import javax.swing.ProgressMonitor
import javax.swing.ProgressMonitorInputStream
import javax.swing.SwingWorker
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.jvm.isAccessible
import kotlinx.serialization.toUtf8Bytes
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import org.joor.Reflect
import org.oxbow.swingbits.dialog.task.TaskDialogs

object PackSquashStep : BulkExportStep() {
    private val logger = LogManager.getLogger()

    override fun getName(): String = "PackSquash"
    override fun getType(): ExportStepType = ExportStepType.ZIPPER

    override fun processFile(
        file: File,
        progressMonitor: ProgressMonitor
    ) {
        var progress = 0
        progressMonitor.maximum = Files.walk(file.toPath()).count().toInt()

        val settings = ConfigUtil.getSettings<PackSquashStepSettings>("deflatedpickle@pack_squash_step#1.0.0")

        val arguments = """
            |resource_pack_directory = "."
            |output_file_path = "${file}/${file.nameWithoutExtension}.zip"
            |${File(settings.settings).readText()}
            """.trimMargin()
        // println(arguments)

        this.logger.debug("Starting the PackSquash command")
        val process = ProcessBuilder(
            "${File(".").canonicalPath}/${settings.location}/${settings.executable}" + if (OSUtil.isWindows()) ".exe" else "",
            "-"
        )
            .directory(File(file.path))
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start().apply {
                try {
                    if (this.isAlive) {
                        for (line in arguments.lines()) {
                            outputStream.write(
                                "$line${System.lineSeparator()}".toUtf8Bytes()
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

        Thread("PackSquash Feed") {
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
            } catch (e: IOException) {
            }
            progressMonitor.close()
        }.start()
        this.logger.debug("Started the PackSquash thread")
    }
}