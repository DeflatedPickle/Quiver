package com.deflatedpickle.quiver.packexport.api

import java.io.File

/**
 * A step in pack exporting
 */
// More like export step on me, amirite
interface ExportStep {
    /**
     * This explains how the file is manipulated by this step
     */
    fun processFile(file: File)
}