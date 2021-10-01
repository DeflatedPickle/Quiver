/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.alexandriasoftware.swing.JSplitButton
import com.athaydes.kunion.Union
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.filepanel.FilePanel
import com.deflatedpickle.quiver.filetable.config.FileTableSettings
import com.deflatedpickle.quiver.filetable.util.FileLinkAction
import com.deflatedpickle.quiver.filewatcher.event.EventFileSystemUpdate
import com.deflatedpickle.quiver.frontend.menu.LinkedFilesPopupMenu
import com.deflatedpickle.quiver.frontend.widget.SearchToolbar
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.extensions.updateUIRecursively
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.Timer

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        Provides a table to show what files are in a given folder
    """,
    type = PluginType.COMPONENT,
    component = Component::class,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0",
        "deflatedpickle@file_watcher"
    ],
    settings = FileTableSettings::class
)
object FileTablePlugin {
    private val toolbar = SearchToolbar(Union.U3.ofB(FileTable))

    private val fileLinkMenu = LinkedFilesPopupMenu {
        if (FileTable.selectedRow >= 0)
            FileTable.getValueAt(FileTable.selectedRow, 0) as File?
        else null
    }

    private val linkButton = JSplitButton("  ", MonoIcon.CHAIN_LINK).apply {
        popupMenu = fileLinkMenu
        isEnabled = false
    }

    init {
        EventProgramFinishSetup.addListener {
            ConfigUtil.getSettings<FileTableSettings>(
                "deflatedpickle@file_table#>=1.0.1"
            )?.let { settings ->
                when (settings.noFileLinkAction) {
                    FileLinkAction.REMOVE -> this.linkButton.isEnabled = true
                    FileLinkAction.DISABLE -> FilePanel.fileActionPanel.add(this.linkButton)
                }
            }

            Component.add(this.toolbar, BorderLayout.NORTH)
        }

        EventOpenPack.addListener {
            FileTable.refresh(it)
        }

        EventFileSystemUpdate.addListener { file ->
            // Waiting until Haruhi updates events to have sources
            if (file.isFile || !file.exists()) {
                // FileTable.refresh(file)
                EventSelectFolder.trigger(Quiver.packDirectory!!)
                EventSearchFile.trigger(file)
            }
        }

        EventSelectFolder.addListener {
            // GlobalScope.launch {
                FileTable.refresh(it)
           //  }
        }

        EventSelectFile.addListener {
            // Definitely not the most efficient solution ¯\_(ツ)_/¯
            fileLinkMenu.validateMenu()

            ConfigUtil.getSettings<FileTableSettings>(
                "deflatedpickle@file_table#>=1.0.1"
            )?.let { settings ->
                when (settings.noFileLinkAction) {
                    FileLinkAction.REMOVE -> {
                        if (!this.linkButton.isEnabled) {
                            this.linkButton.isEnabled = true
                        }

                        if (this.fileLinkMenu.componentCount > 0)
                            FilePanel.fileActionPanel.add(linkButton)
                        else FilePanel.fileActionPanel.remove(linkButton)

                        FilePanel.fileActionPanel.apply {
                            repaint()
                            revalidate()
                        }
                    }
                    FileLinkAction.DISABLE -> {
                        if (!FilePanel.fileActionPanel.components.contains(linkButton)) {
                            FilePanel.fileActionPanel.add(linkButton)
                        }

                        this.linkButton.isEnabled =
                            this.fileLinkMenu.componentCount > 0
                    }
                }
            }
        }

        EventSearchFile.addListener {
            for ((i, value) in FileTable.fileModel.dataVector.toList().withIndex()) {
                if ((value as List<*>)[0] == it) {
                    val index = FileTable.convertRowIndexToView(i)
                    FileTable.setRowSelectionInterval(index, index)
                    FileTable.scrollRowToVisible(index)
                    break
                }
            }
        }

        EventChangeTheme.addListener {
            fileLinkMenu.updateUI()
            linkButton.updateUI()

            SwingUtilities.invokeLater {
                FileTable.resetHighlighters()
            }
        }
    }
}
