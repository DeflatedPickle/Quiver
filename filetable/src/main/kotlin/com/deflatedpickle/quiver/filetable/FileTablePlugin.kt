/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.filepanel.FilePanel
import com.deflatedpickle.quiver.frontend.menu.LinkedFilesPopupMenu
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
        FilePanel.fileActionPanel.add(JSplitButton("Linked  ").apply {
            popupMenu = LinkedFilesPopupMenu {
                if (FileTable.selectedRow >= 0)
                    FileTable.fileModel.getValueAt(FileTable.selectedRow, 0) as File?
                else null
            }
        })

        EventSelectFolder.addListener {
            FileTable.refresh(it)
        }

        EventOpenFile.addListener {
            this.currentDir = it
            FileTable.refreshAll()
        }
    }
}
