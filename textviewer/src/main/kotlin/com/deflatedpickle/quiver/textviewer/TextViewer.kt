package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil

@Suppress("unused")
@Plugin(
    value = "text_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for text-based files
    """,
    type = PluginType.OTHER
)
object TextViewer {
    private val extensionSet = setOf(
        "txt",
        "json",
        "js", "ts",
        "gitignore",
        "md",
        "bat", "py", "rb",
        "make", "makefile", "makef", "gmk", "mak",
        "gradle"
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