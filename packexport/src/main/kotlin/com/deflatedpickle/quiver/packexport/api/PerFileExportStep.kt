package com.deflatedpickle.quiver.packexport.api

import java.io.File

abstract class PerFileExportStep(
    /**
     * The kinds of files this step will be applied to
     *
     * Example;
     * - png
     * - txt
     * - *
     */
    val affectedExtensions: Collection<String>
) : ExportStep