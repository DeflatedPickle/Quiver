/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel
import org.jdesktop.swingx.JXTable

class Component : JXTable() {
    val fileModel = DefaultTableModel(arrayOf(""), 0)

    init {
        EventProgramFinishSetup.addListener {
            this.fileModel.setDataVector(
                arrayOf(),
                arrayOf(
                    "Key",
                    "Value"
                )
            )
        }

        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
    }
}
