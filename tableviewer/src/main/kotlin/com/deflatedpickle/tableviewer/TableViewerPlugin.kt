/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer

@Suppress("unused")
@Plugin(
    value = "table_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for lang and properties files
    """,
    type = PluginType.OTHER
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
                    val ext = """.*\.$i"""

                    if (registry.get(ext) == null) {
                        registry.register(ext, mutableListOf(TableViewer as Viewer<Any>))
                    } else {
                        registry.get(ext)!!.add(TableViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
