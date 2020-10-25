/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import java.io.File

@Suppress("unused")
@Plugin(
    value = "file_table",
    author = "DeflatedPickle",
    version = "1.0.1",
    description = """
        <br>
        Provides a table to show what files are in a given folder
    """,
    type = PluginType.COMPONENT,
    component = Component::class
)
object FileTablePlugin {
    var currentDir: File? = null

    init {
        EventSelectFolder.addListener {
            FileTable.refresh(it)
        }

        EventOpenFile.addListener {
            this.currentDir = it
            FileTable.refreshAll()
        }
    }
}
