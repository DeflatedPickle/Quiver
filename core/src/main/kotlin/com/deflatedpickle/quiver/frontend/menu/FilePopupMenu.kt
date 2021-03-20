/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.menu

import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.frontend.extension.disableAll
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JMenu
import javax.swing.JPopupMenu

// TODO: Add more items to FilePopupMenu, such as Cut, Copy, Paste, Replace, Delete
class FilePopupMenu(
    val getFile: () -> File?
) : JPopupMenu() {
    private val openItem = this.add("Open") {
        Desktop.getDesktop().open(this.getFile())
    }

    private val editItem = this.add("Edit") {
        try {
            Desktop.getDesktop().edit(this.getFile())
        } catch (e: IOException) {
            Desktop.getDesktop().open(this.getFile())
        }
    }

    private val copyItem = JMenu("Copy").also { add(it) }
    private val copyName = copyItem.add("Name") {
        this.getFile()?.nameWithoutExtension?.let { copyText(it) }
    }
    private val copyContent = copyItem.add("Content") {
        this.getFile()?.let {
            when (it.extension) {
                else -> copyText(it.readText())
            }
        }
    }

    private val copySizeItem = JMenu("Size").also { copyItem.add(it) }
    private val copyWidth = copySizeItem.add("Width") {
        this.getFile()?.let { copyText(ImageIO.read(it).width.toString()) }
    }
    private val copyHeight = copySizeItem.add("Height") {
        this.getFile()?.let { copyText(ImageIO.read(it).height.toString()) }
    }

    override fun setVisible(b: Boolean) {
        // Disable everything before we validate
        this.disableAll()

        val file = this.getFile()

        // Disable some parts if they're a file or directory
        if (file != null) {
            this.openItem.isEnabled = true
            this.copyItem.isEnabled = true
            this.copyName.isEnabled = true

            if (file.isFile) {
                this.editItem.isEnabled = true

                when (file.extension) {
                    "png" -> {
                        this.copySizeItem.isEnabled = true
                        this.copyWidth.isEnabled = true
                        this.copyHeight.isEnabled = true
                    }
                    else -> {
                        this.copyContent.isEnabled = true
                    }
                }
            }
        }

        super.setVisible(b)
    }

    private fun copyText(string: String) {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(
                StringSelection(string),
                null
            )
    }
}
