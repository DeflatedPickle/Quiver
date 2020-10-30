/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.foldertree

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.backend.event.EventSearchFolder
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

@Suppress("unused")
@Plugin(
    value = "folder_tree",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a panel on which a given file can be configured
    """,
    type = PluginType.COMPONENT,
    component = Component::class
)
object FolderTreePlugin {
    init {
        EventNewDocument.addListener {
            FolderTree.refreshAll()
        }

        EventOpenFile.addListener {
            FolderTree.refreshAll()
        }

        EventSearchFolder.addListener {
            var parent: File? = it.parentFile
            val selectPath = mutableListOf(it)

            while (parent != null) {
                selectPath.add(0, parent)

                parent = if (parent.path != DocumentUtil.current!!.path) {
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
