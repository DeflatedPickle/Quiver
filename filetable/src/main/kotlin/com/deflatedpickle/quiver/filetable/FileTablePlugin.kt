/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.haruhi.event.EventPanelFocusGained
import com.deflatedpickle.quiver.backend.event.EventOpenFile
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
        EventCreateFile.addListener {
            FileTable.refreshAll()
        }

        EventOpenFile.addListener {
            this.currentDir = it
            FileTable.refreshAll()
        }

        EventPanelFocusGained.addListener {
            if (it == Component) {
                FileTable.refreshAll()
            }
        }
    }
}
