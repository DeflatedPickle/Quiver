package com.deflatedpickle.quiver.packexport.api

/**
 * A step during exporting that is applied to [affectedExtensions]
 */
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
) : ExportStep {
    override fun toString(): String = "PerFileExportStep( ${getName()}: ${getType()}; $affectedExtensions )"
}