/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.filepanel.api.Viewer

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
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
        "mcmeta", "json", // JSON-like
        "nbt", "dat" // NBT-like
    )

    init {
        EventOpenPack.addListener {
            if (Quiver.format > 3) {
                val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

                registry?.getOrRegister("lang", ::mutableListOf)?.let { it += TreeViewer as Viewer<Any> }
            }
        }

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?)?.let { registry ->
                for (i in this.extensionSet) {
                    registry.getOrRegister(i, ::mutableListOf)?.let { it += TreeViewer as Viewer<Any> }
                }
            }
        }
    }
}
