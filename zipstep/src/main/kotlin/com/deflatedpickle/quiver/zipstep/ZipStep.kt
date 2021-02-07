package com.deflatedpickle.quiver.zipstep

import net.lingala.zip4j.progress.ProgressMonitor as ZipProgressMonitor
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.Slug
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.marvin.extensions.Thread
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import com.deflatedpickle.quiver.packexport.api.ExportStepType
import java.io.File
import javax.swing.ProgressMonitor
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import org.apache.logging.log4j.LogManager

object ZipStep : BulkExportStep() {
    private val logger = LogManager.getLogger()

    override fun getSlug(): Slug = Slug(
        "DeflatedPickle",
        "Zip",
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
        progressMonitor.maximum = 100

        val settings = ConfigUtil.getSettings<ZipStepSettings>("deflatedpickle@zip_step#1.0.0")

        val parameters = ZipParameters().apply {
            compressionMethod = settings.compressionMethod
            compressionLevel = settings.compressionLevel
            isReadHiddenFiles = settings.readHiddenFiles
            isReadHiddenFolders = settings.readHiddenFolders
            isWriteExtendedLocalFileHeader = settings.writeExtendedLocalFileHeader
            fileComment = settings.fileComment
            symbolicLinkAction = settings.symbolicLinkAction
            isUnixMode = settings.unixMode
        }

        val path = "${file.parentFile.absolutePath}/${file.nameWithoutExtension}.zip"
        // We have to delete the file first,
        // or we run into concurrent modification error's
        File(path).delete()
        val zipFile = ZipFile(path)

        val zipProgressMonitor = zipFile.progressMonitor
        zipFile.isRunInThread = true

        Thread("${getSlug().name} Feed") {
            // For whatever reason, the progress *starts* at 100, so we can't check for that
            while (zipProgressMonitor.percentDone != 99) {
                if (progressMonitor.isCanceled) {
                    zipProgressMonitor.isCancelAllTasks = true
                    zipProgressMonitor.result = ZipProgressMonitor.Result.CANCELLED
                    break
                }

                logger.trace("Zipping $file: ${zipProgressMonitor.fileName} - ${zipProgressMonitor.percentDone}/100")
                progressMonitor.note = zipProgressMonitor.fileName
                progressMonitor.setProgress(zipProgressMonitor.percentDone + 1)
            }

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (zipProgressMonitor.result) {
                ZipProgressMonitor.Result.SUCCESS ->
                    logger.info("The ${getSlug().name} task finished successfully")
                ZipProgressMonitor.Result.ERROR ->
                    logger.error("The ${getSlug().name} task ran into the error; ${zipProgressMonitor.exception.message}")
                ZipProgressMonitor.Result.CANCELLED ->
                    logger.warn("The ${getSlug().name} task was cancelled")
            }
        }.start()

        // I've done it like this rather than explicit file names in case anyone includes extra files,
        // like a README or LICENSE
        file.listFiles()?.sortedBy { it.isDirectory }?.let { folder ->
            for (f in folder) {
                when {
                    f.isFile -> zipFile.addFile(f, parameters)
                    f.isDirectory -> zipFile.addFolder(f, parameters)
                }
            }
        }
    }
}