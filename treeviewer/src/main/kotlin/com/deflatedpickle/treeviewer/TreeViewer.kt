package com.deflatedpickle.treeviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil

@Suppress("unused")
@Plugin(
    value = "tree_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for JSON-like files
    """,
    type = PluginType.OTHER
)
object TreeViewer {
    private val extensionSet = setOf(
        "mcmeta",
        "lang",
        "json"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer")

            if (registry != null) {
                for (i in extensionSet) {
                    registry.register(i, Viewer)
                }
            }
        }
    }
}