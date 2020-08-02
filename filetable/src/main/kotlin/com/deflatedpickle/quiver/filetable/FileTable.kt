package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.quiver.backend.event.EventOpenFile

@Suppress("unused")
@Plugin(
    value = "file_table",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a table to show what files are in a given folder
    """,
    type = PluginType.COMPONENT,
    component = Component::class
)
object FileTable {
    init {
        EventCreateFile.addListener {
            Table.refreshAll()
        }

        EventOpenFile.addListener {
            Table.refreshAll()
        }
    }
}