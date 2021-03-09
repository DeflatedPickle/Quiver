/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.menu

import com.deflatedpickle.quiver.frontend.extension.add
import java.awt.Desktop
import java.io.File
import javax.swing.JPopupMenu

// TODO: Add more items to FilePopupMenu, such as Cut, Copy, Paste, Replace, Delete
class FilePopupMenu(
    val getFile: () -> File?
) : JPopupMenu() {
    private val openItem = this.add("Open") {
        Desktop.getDesktop().open(this.getFile())
    }

    private val editItem = this.add("Edit") {
        Desktop.getDesktop().edit(this.getFile())
    }

    init {
        this.add(openItem)
        this.add(editItem)
    }

    override fun setVisible(b: Boolean) {
        val file = this.getFile()

        // Disable some parts if they're a file or directory
        if (file != null) {
            if (file.isFile) {
                this.openItem.isEnabled = true
                this.editItem.isEnabled = true
            } else if (file.isDirectory) {
                this.openItem.isEnabled = true
                this.editItem.isEnabled = false
            }
        } else {
            this.openItem.isEnabled = false
            this.editItem.isEnabled = false
        }

        super.setVisible(b)
    }
}
