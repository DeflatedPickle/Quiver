package com.deflatedpickle.quiver.packexport.api

/**
 * A step during exporting that is run on the pack as a whole
 */
abstract class BulkExportStep : ExportStep {
    override fun toString(): String = "BulkExportStep( ${getName()}: ${getType()} )"
}