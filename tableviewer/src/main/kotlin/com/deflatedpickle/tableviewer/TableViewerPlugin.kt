/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.filepanel.api.Viewer

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A viewer for lang and properties files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
object TableViewerPlugin {
    private val extensionSet = setOf(
        "properties"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        registry.register(i, mutableListOf(TableViewer as Viewer<Any>))
                    } else {
                        registry.get(i)!!.add(TableViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
