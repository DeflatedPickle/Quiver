package com.deflatedpickle.imageviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil

@Suppress("unused")
@Plugin(
    value = "image_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for png files
    """,
    type = PluginType.OTHER
)
object ImageViewer {
    private val extensionSet = setOf(
        "png"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer")

            if (registry != null) {
                for (i in this.extensionSet) {
                    registry.register(i, Viewer)
                }
            }
        }
    }
}