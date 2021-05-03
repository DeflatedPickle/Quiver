/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.imageviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.filepanel.api.Viewer

@Suppress("unused")
@Plugin(
    value = "image_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for PNG files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
// TODO: Support image animation interpolation
object ImageViewerPlugin {
    private val extensionSet = setOf(
        "png",
        "jpg", "jpeg"
    )

    init {
        EventProgramFinishSetup.addListener {
            @Suppress("UNCHECKED_CAST")
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        @Suppress("UNCHECKED_CAST")
                        registry.register(i, mutableListOf(ImageViewer as Viewer<Any>))
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        registry.get(i)!!.add(ImageViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
