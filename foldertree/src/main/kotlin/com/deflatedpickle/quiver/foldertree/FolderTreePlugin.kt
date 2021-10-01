/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.foldertree

import com.athaydes.kunion.Union
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.backend.event.EventSearchFolder
import com.deflatedpickle.quiver.filewatcher.event.EventFileSystemUpdate
import com.deflatedpickle.quiver.frontend.widget.SearchToolbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.EventQueue
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath
import kotlin.io.path.ExperimentalPathApi

@OptIn(ExperimentalPathApi::class)
@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        Provides a panel on which a given file can be configured
    """,
    type = PluginType.COMPONENT,
    component = Component::class,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0",
        "deflatedpickle@file_watcher"
    ]
)
object FolderTreePlugin {
    private val toolbar = SearchToolbar(Union.U3.ofA(FolderTree))

    init {
        EventProgramFinishSetup.addListener {
            Component.add(this.toolbar, BorderLayout.NORTH)
        }

        EventOpenPack.addListener {
            FolderTree.refreshAll()
        }

        EventFileSystemUpdate.addListener { file ->
            // Waiting until Haruhi updates events to have sources
            if (file.isDirectory || !file.exists()) {
                GlobalScope.launch {
                    FolderTree.refreshAll()
                }
            }
        }

        EventSearchFolder.addListener {
            var parent: File? = it.parentFile
            val selectPath = mutableListOf(it)

            while (parent != null) {
                selectPath.add(0, parent)

                parent = if (parent.path != Quiver.packDirectory!!.path) {
                    parent.parentFile
                } else {
                    null
                }
            }

            // This selects the right folder, but not visually
            FolderTree.selectionPath = TreePath(
                selectPath.map { file ->
                    DefaultMutableTreeNode(file)
                }.toTypedArray()
            )
        }
    }
}
