package com.deflatedpickle.quiver.packexport.api

abstract class BulkExportStep(
    /**
     * Whether or not to run this step before zipping up the pack
     */
    val beforeZip: Boolean = false
) : ExportStep