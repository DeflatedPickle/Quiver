/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
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

    private val fileLinkMenu = LinkedFilesPopupMenu {
        if (FileTable.selectedRow >= 0)
            FileTable.fileModel.getValueAt(FileTable.selectedRow, 0) as File?
        else null
    }

    private val linkButton = JSplitButton("Linked  ").apply {
        popupMenu = fileLinkMenu
        isEnabled = false
    }

    init {
        FilePanel.fileActionPanel.add(linkButton)

        EventSelectFolder.addListener {
            FileTable.refresh(it)
        }

        EventSelectFile.addListener {
            // Definitely not the most efficient solution ¯\_(ツ)_/¯
            fileLinkMenu.validateMenu()
            this.linkButton.isEnabled = this.fileLinkMenu.componentCount > 0
        }

        EventOpenFile.addListener {
            this.currentDir = it
            FileTable.refreshAll()
        }

        EventSearchFile.addListener {
            for ((index, value) in FileTable.fileModel.dataVector.toList().withIndex()) {
                if ((value as List<*>)[0] == it) {
                    FileTable.setRowSelectionInterval(index, index)
                    break
                }
            }
        }
    }
}
