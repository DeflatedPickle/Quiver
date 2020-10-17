package com.deflatedpickle.quiver.frontend.menu

import com.deflatedpickle.quiver.frontend.extension.add
import java.awt.Desktop
import java.io.File
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class FilePopupMenu(
    val getFile: () -> File?
) : JPopupMenu() {
    private val openItem = this.add("Open") {
        Desktop.getDesktop().open(this.getFile())
    }

    private val editItem = this.add("Edit") {
        Desktop.getDesktop().open(this.getFile())
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