package com.deflatedpickle.quiver.packexport.api

import com.deflatedpickle.marvin.Slug
import java.io.File
import javax.swing.ProgressMonitor

/**
 * A step in pack exporting
 */
// More like export step on me, amirite
interface ExportStep {
    /**
     * Get the slug for this plugin
     */
    fun getSlug(): Slug

    /**
     * The type of export this is
     */
    fun getType(): ExportStepType

    /**
     * A collection of [Slug]'s this [ExportStep] isn't compatible with
     */
    fun getIncompatibleSlugs(): Collection<Slug> = listOf()

    /**
     * A collection of [ExportStepType]'s this [ExportStep] isn't compatible with
     */
    fun getIncompatibleTypes(): Collection<ExportStepType> = listOf()

    /**
     * This explains how the file is manipulated by this step
     */
    fun processFile(file: File, progressMonitor: ProgressMonitor)
}