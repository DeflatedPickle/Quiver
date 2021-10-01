/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver

import blue.endless.jankson.Jankson
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.backend.util.PackFormat
import com.deflatedpickle.quiver.backend.util.lang.JsonLangReader
import com.deflatedpickle.quiver.backend.api.lang.LangReader
import com.deflatedpickle.quiver.backend.util.lang.PropertiesLangReader
import java.io.File

@Suppress("unused", "SpellCheckingInspection")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A program for creating Minecraft resource-packs
    """,
    type = PluginType.CORE_API
)
object Quiver {
    internal val json = Jankson.builder().build()

    var packDirectory: File? = null

    var selectedDir: File? = null
        private set

    var selectedFile: File? = null
        private set

    var resolution = -1
    var format: PackFormat = -1

    init {
        LangReader.registry.register("properties", PropertiesLangReader)
        LangReader.registry.register("json", JsonLangReader)

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
