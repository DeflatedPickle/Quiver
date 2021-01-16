package com.deflatedpickle.quiver.packsquashstep

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import java.io.File
import java.util.concurrent.TimeUnit
import org.apache.logging.log4j.LogManager

object PackSquashStep : BulkExportStep() {
    private val logger = LogManager.getLogger()

    override fun processFile(file: File) {
        val settings = ConfigUtil.getSettings<PackSquashStepSettings>("deflatedpickle@pack_squash_step#1.0.0")

        this.logger.debug("Starting the PackSquash command")
        ProcessBuilder(
            "${settings.location}/${settings.executable}",
            file.path,
            "${file.parent}/${file.nameWithoutExtension}.zip"
        ).inheritIO().start().waitFor(settings.timeout, TimeUnit.SECONDS)
    }
}