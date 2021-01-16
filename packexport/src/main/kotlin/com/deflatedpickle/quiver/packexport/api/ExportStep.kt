package com.deflatedpickle.quiver.packexport.api

import java.io.File

/**
 * A step in pack exporting
 */
// More like export step on me, amirite
interface ExportStep : Comparable<ExportStep> {
    /**
     * Gets the name of this export step
     */
    fun getName(): String
    /**
     * This explains how the file is manipulated by this step
     */
    fun processFile(file: File)

    override fun compareTo(other: ExportStep): Int {
        if (other is PerFileExportStep) {
            return 1
        }
        return 0
    }
}