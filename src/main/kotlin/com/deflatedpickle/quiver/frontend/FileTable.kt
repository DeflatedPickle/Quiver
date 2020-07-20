package com.deflatedpickle.quiver.frontend

import org.jdesktop.swingx.JXTable
import java.io.File
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

object FileTable : JXTable() {
    val headers = arrayOf("Name", "Extension")
    val fileModel = DefaultTableModel(headers, 0)

    init {
        this.model = fileModel
        this.isEditable = false

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        this.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                if (this.selectedRow >= 0) {
                    val value = this.fileModel.getValueAt(
                        this.selectedRow, 0
                    )

                    if (value is File && value.exists() && value.isFile && !value.isHidden) {
                        FilePanel.state(true)
                    }
                } else {
                    FilePanel.state(false)
                }
            }
        }
    }

    fun refresh(file: File) {
        this.fileModel.rowCount = 0

        file.listFiles()?.filter { !it.isDirectory }?.forEach {
            this.fileModel.addRow(arrayOf(it, it.extension))
        }

        if (this.rowCount > 0) {
            this.setRowSelectionInterval(0, 0)
        }
    }
}