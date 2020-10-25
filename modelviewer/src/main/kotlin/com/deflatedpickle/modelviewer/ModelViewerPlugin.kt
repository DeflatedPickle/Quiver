package com.deflatedpickle.modelviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer

@Suppress("unused")
@Plugin(
    value = "model_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for json and obj files
    """,
    type = PluginType.OTHER
)
object ModelViewerPlugin {
    private val extensionSet = setOf(
        "json",
        "obj"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        registry.register(i, mutableListOf(ModelViewer as Viewer<Any>))
                    } else {
                        registry.get(i)!!.add(ModelViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}