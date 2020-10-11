package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.filepanel.Component
import org.jdesktop.swingx.JXTable
import java.io.File
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

object Table : JXTable() {
    private val fileModel = object : DefaultTableModel(arrayOf(""), 0) {
        override fun getColumnClass(columnIndex: Int): Class<*> =
            when(columnIndex) {
                0 -> File::class.java
                else -> String::class.java
            }
    }

    init {
        EventProgramFinishSetup.addListener {
            val lang = LangUtil.getLang("deflatedpickle@file_table#1.0.1")

            this.fileModel.setDataVector(
                arrayOf(),
                arrayOf(
                    lang.trans("table.header.name"),
                    lang.trans("table.header.extension")
                )
            )
        }

        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

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
                        !value.isHidden
                    ) {
                        Component.state(true)
                        EventSelectFile.trigger(value)
                    }
                } else {
                    Component.state(false)
                }
            }
        }

        this.setDefaultRenderer(File::class.java, object : DefaultTableCellRenderer() {
            override fun getTableCellRendererComponent(
                table: JTable, value: Any,
                isSelected: Boolean, hasFocus: Boolean,
                row: Int, column: Int
            ): java.awt.Component = super.getTableCellRendererComponent(
                table, (value as File).nameWithoutExtension,
                isSelected, hasFocus,
                row, column
            )
        })
    }

    fun refreshAll() {
        this.removeAll()

        val document = DocumentUtil.current
        refresh(document!!)
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