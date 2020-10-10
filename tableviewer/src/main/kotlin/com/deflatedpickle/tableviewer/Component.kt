package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import org.jdesktop.swingx.JXTable
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

class Component : JXTable() {
    val fileModel = DefaultTableModel(arrayOf(""), 0)

    init {
        EventProgramFinishSetup.addListener {
            val lang = LangUtil.getLang("deflatedpickle@table_viewer#1.0.0")

            this.fileModel.setDataVector(
                arrayOf(),
                arrayOf(
                    lang.trans("table.header.key"),
                    lang.trans("table.header.value")
                )
            )
        }

        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS
    }
}