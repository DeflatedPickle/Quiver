package com.deflatedpickle.quiver.frontend.menu

import blue.endless.jankson.Jankson
import com.deflatedpickle.quiver.frontend.extension.add
import java.io.File
import javax.swing.JMenu
import javax.swing.JPopupMenu

class LinkedFilesPopupMenu(
    val getFile: () -> File?
) : JPopupMenu("Linked Files") {
    private val json = Jankson.builder().build()

    private val texturesMenu = JMenu("Textures")

    override fun setVisible(b: Boolean) {
        this.removeAll()
        this.texturesMenu.removeAll()

        val file = this.getFile()

        if (file != null && file.isFile) {
            when (file.extension) {
                "png" -> handlePNG(file)
                "json" -> handleJSON(file)
                "mcmeta" -> handleMeta(file)
            }
        }

        super.setVisible(b)
    }

    private fun handlePNG(file: File) {
        val parentFile = file.parentFile.resolve("${file.name}.mcmeta")

        if (parentFile.exists() && parentFile.isFile) {
            this.add("Meta") {
            }
        }
    }

    private fun handleMeta(file: File) {
        val parentFile = file.parentFile.resolve("${file.name.split(".")[0]}.png")

        if (parentFile.exists() && parentFile.isFile) {
            this.add("Texture") {
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
                entries.size == 1 -> this.add("Texture") {
                }
                entries.size > 1 -> {
                    for ((key, value) in this.json
                        .load(file)
                        .getObject("textures")!!.entries) {
                        this.texturesMenu.add(key) {
                        }
                    }
                    this.add(this.texturesMenu)
                }
            }
        }
    }
}