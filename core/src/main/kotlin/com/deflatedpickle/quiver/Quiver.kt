/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import java.io.File

@Suppress("unused", "SpellCheckingInspection")
@Plugin(
    value = "quiver",
    author = "DeflatedPickle",
    version = "1.7.0",
    description = """
        <br>
        A program for creating Minecraft resource-packs
    """,
    type = PluginType.CORE_API
)
object Quiver {
    var packDirectory: File? = null

    var selectedDir: File? = null
        private set

    var selectedFile: File? = null
        private set

    init {
        EventNewDocument.addListener {
            this.packDirectory = it
            this.selectedDir = it
        }

        EventSelectFile.addListener {
            this.selectedFile = it
        }

        EventSelectFolder.addListener {
            this.selectedDir = it
        }
    }
}
