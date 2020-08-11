package com.deflatedpickle.tableviewer

import org.jdesktop.swingx.JXTable
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

class Component : JXTable() {
    private val headers = arrayOf("Key", "Value")
    val fileModel = DefaultTableModel(headers, 0)

    init {
        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
    }
}