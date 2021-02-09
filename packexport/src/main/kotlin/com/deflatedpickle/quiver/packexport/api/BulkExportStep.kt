/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.packexport.api

/**
 * A step during exporting that is run on the pack as a whole
 */
abstract class BulkExportStep : ExportStep {
    override fun toString(): String = "BulkExportStep( ${getSlug()}: ${getType()} )"
}
