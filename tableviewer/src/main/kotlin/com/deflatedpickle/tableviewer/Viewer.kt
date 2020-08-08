package com.deflatedpickle.tableviewer

import org.jdesktop.swingx.JXTable
import java.io.File
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

object Viewer : JXTable() {
    private val headers = arrayOf("Key", "Value")
    private val fileModel = DefaultTableModel(headers, 0)

    init {
        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
    }

    fun refresh(file: File) {
        fileModel.rowCount = 0

        for (line in file.readLines()) {
            fileModel.addRow(line.split("=").toTypedArray())
        }

        if (this.rowCount > 0) {
            this.setRowSelectionInterval(0, 0)
        }
    }
}