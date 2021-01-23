package com.deflatedpickle.quiver.packexport.event

import com.deflatedpickle.haruhi.api.event.AbstractEvent
import java.io.File

/**
 * An event that is run when a file has been exported
 */
object EventExportFile : AbstractEvent<File>()