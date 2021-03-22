/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer

@Suppress("unused")
@Plugin(
    value = "text_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for text-based files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
object TextViewerPlugin {
    private val extensionSet = setOf(
        "",
        "mcmeta",
        "txt",
        "json",
        "js", "ts",
        "gitignore",
        "bat", "py", "rb",
        "make", "makefile", "makef", "gmk", "mak",
        "gradle"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        registry.register(i, mutableListOf(TextViewer as Viewer<Any>))
                    } else {
                        registry.get(i)!!.add(TextViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
