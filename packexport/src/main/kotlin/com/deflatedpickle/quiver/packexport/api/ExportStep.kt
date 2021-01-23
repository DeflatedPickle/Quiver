package com.deflatedpickle.quiver.packexport.api

import java.io.File
import javax.swing.ProgressMonitor

/**
 * A step in pack exporting
 */
// More like export step on me, amirite
interface ExportStep {
    /**
     * Gets the name of this export step
     */
    fun getName(): String

    /**
     * The type of export this is
     */
    fun getType(): ExportStepType

    /**
     * A collection of [ExportStepType]'s this [ExportStepType] isn't compatible with
     */
    fun getIncompatibleTypes(): Collection<ExportStepType> = listOf()

    /**
     * This explains how the file is manipulated by this step
     */
    fun processFile(file: File, progressMonitor: ProgressMonitor)
}