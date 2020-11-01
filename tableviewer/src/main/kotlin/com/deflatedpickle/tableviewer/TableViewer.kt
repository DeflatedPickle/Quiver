/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.tableviewer

import com.deflatedpickle.quiver.backend.api.Viewer
import java.io.File
import javax.swing.JComponent
import javax.swing.JScrollPane

object TableViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        component.fileModel.rowCount = 0

        for (line in with.readLines()) {
            component.fileModel.addRow(line.split("=").toTypedArray())
        }

        if (this.component.rowCount > 0) {
            this.component.setRowSelectionInterval(0, 0)
        }
    }

    override fun getComponent(): JComponent = this.component
    override fun getScroller(): JScrollPane = JScrollPane(this.getComponent())
}
