/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.menu

import blue.endless.jankson.Jankson
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSearchFolder
import com.deflatedpickle.undulation.extensions.add
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
            this.add("Meta", NagatoIcon.FILE_JSON) {
                EventSearchFile.trigger(targetFile)
            }
        }
    }

    private fun handleMeta(file: File) {
        val targetFile = file.parentFile.resolve("${file.name.split(".")[0]}.png")

        if (targetFile.exists() && targetFile.isFile) {
            this.add("Texture", NagatoIcon.PAINTING_BLANK) {
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
                entries.size == 1 -> this.add("Texture", NagatoIcon.PAINTING_BLANK) {
                    this.searchTexture(
                        jsonObject.get(
                            String::class.java,
                            entries.elementAt(0).key
                        )!!
                    )
                }
                entries.size > 1 -> {
                    for ((key, _) in this.json
                        .load(file)
                        .getObject("textures")!!.entries) {
                        this.texturesMenu.add(key, NagatoIcon.PAINTING_BLANK) {
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

    private fun searchTexture(value: String) {
        val split = value.split(":")
        val textureFile = Quiver.packDirectory!!
            .resolve("assets")
            .resolve(split[0])
            .resolve("textures")
            .resolve("${split[1]}.png")

        EventSearchFolder.trigger(textureFile.parentFile)
        EventSearchFile.trigger(textureFile)
    }
}
