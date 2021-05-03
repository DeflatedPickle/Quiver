/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.filepanel.api.Viewer

@Suppress("unused")
@Plugin(
    value = "tree_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for JSON-like files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
object TreeViewerPlugin {
    private val extensionSet = setOf(
        "mcmeta",
        "lang",
        "json"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in extensionSet) {
                    if (registry.get(i) == null) {
                        registry.register(i, mutableListOf(TreeViewer as Viewer<Any>))
                    } else {
                        registry.get(i)!!.add(TreeViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
