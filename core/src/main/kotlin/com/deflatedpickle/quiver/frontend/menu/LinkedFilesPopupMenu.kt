/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.menu

import blue.endless.jankson.Jankson
import com.deflatedpickle.mmf.util.AssetPath
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSearchFolder
import com.deflatedpickle.undulation.extensions.add
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.File
import javax.swing.JMenu
import javax.swing.JPopupMenu

class LinkedFilesPopupMenu(
    val getFile: () -> File?
) : JPopupMenu("Linked Files") {
    private val json = Jankson.builder().build()

    private val texturesMenu = JMenu("Textures")

    fun validateMenu() {
        this.removeAll()
        this.texturesMenu.removeAll()

        val file = this.getFile()

        if (file != null && file.isFile) {
            when (file.extension) {
                // TODO: Support more file links with an API similar to file viewers
                "png" -> handlePNG(file)
                "json" -> handleJSON(file)
                "mcmeta" -> handleMeta(file)
            }
        }
    }

    override fun setVisible(b: Boolean) {
        this.validateMenu()
        super.setVisible(b)
    }

    private fun handlePNG(file: File) {
        val targetFile = file.parentFile.resolve("${file.name}.mcmeta")

        if (targetFile.exists() && targetFile.isFile) {
            this.add("Meta", MonoIcon.FILE_JSON) {
                EventSearchFile.trigger(targetFile)
            }
        }
    }

    private fun handleMeta(file: File) {
        val targetFile = file.parentFile.resolve("${file.name.split(".")[0]}.png")

        if (targetFile.exists() && targetFile.isFile) {
            this.add("Texture", MonoIcon.PAINTING_BLANK) {
                EventSearchFile.trigger(targetFile)
            }
        }
    }

    private fun handleJSON(file: File) {
        val jsonObject = this.json
            .load(file)
            .getObject("textures")

        if (jsonObject != null) {
            val entries = jsonObject.entries

            when {
                entries.size == 1 -> this.add("Texture", MonoIcon.PAINTING_BLANK) {
                    this.searchTexture(
                        jsonObject.get(
                            String::class.java,
                            entries.elementAt(0).key
                        )!!
                    )
                }
                entries.size > 1 -> {
                    for (
                        (key, _) in this.json
                            .load(file)
                            .getObject("textures")!!.entries
                    ) {
                        this.texturesMenu.add(key, MonoIcon.PAINTING_BLANK) {
                            this.searchTexture(
                                jsonObject.get(
                                    String::class.java,
                                    key
                                )!!
                            )
                        }
                    }
                    this.add(this.texturesMenu)
                }
            }
        }
    }

    @ExperimentalSerializationApi
    private fun searchTexture(value: String) {
        val asset = AssetPath.from(value)
        val textureFile = Quiver.packDirectory!!
            .resolve("assets")
            .resolve(asset.id)
            .resolve("textures")
            .resolve("${asset.asset}.png")

        EventSearchFolder.trigger(textureFile.parentFile)
        EventSearchFile.trigger(textureFile)
    }
}
