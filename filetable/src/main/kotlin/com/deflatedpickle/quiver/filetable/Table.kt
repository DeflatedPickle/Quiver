package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.Component
import org.jdesktop.swingx.JXTable
import java.io.File
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

object Table : JXTable() {
    private val headers = arrayOf("Name", "Extension")
    private val fileModel = DefaultTableModel(headers, 0)

    init {
        this.model = fileModel
        this.isEditable = false

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        this.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                if (this.selectedRow >= 0) {
                    val value = fileModel.getValueAt(
                        this.selectedRow, 0
                    )

                    if (value is File &&
                        value.exists() &&
                        value.isFile &&
                        !value.isHidden) {
                        Component.state(true)
                        EventSelectFile.trigger(value)
                    }
                } else {
                    Component.state(false)
                }
            }
        }
    }

    fun refresh(file: File) {
        fileModel.rowCount = 0

        file.listFiles()?.filter { !it.isDirectory }?.forEach {
            fileModel.addRow(arrayOf(it, it.extension))
        }

        if (this.rowCount > 0) {
            this.setRowSelectionInterval(0, 0)
        }
    }
}