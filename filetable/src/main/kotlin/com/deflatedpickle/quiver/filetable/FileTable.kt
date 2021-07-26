/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.FilePanel
import com.deflatedpickle.quiver.frontend.menu.FilePopupMenu
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXTable
import java.awt.Desktop
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.SortOrder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

object FileTable : JXTable() {
    val fileModel = object : DefaultTableModel(arrayOf(""), 0) {
        override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> File::class.java
                else -> String::class.java
            }
    }

    private val mouseListener = object : MouseAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            if (e.clickCount == 2) {
                Desktop.getDesktop().open(Quiver.selectedFile)
            }
        }
    }

    init {
        EventProgramFinishSetup.addListener {
            this.fileModel.setDataVector(
                arrayOf(),
                arrayOf(
                    "Name",
                    "Extension"
                )
            )
        }

        this.model = fileModel
        this.isEditable = false
        this.selectionMode = ListSelectionModel.SINGLE_SELECTION

        this.autoResizeMode = JTable.AUTO_RESIZE_ALL_COLUMNS

        this.componentPopupMenu = FilePopupMenu {
            if (this.selectedRow >= 0) {
                this.getValueAt(this.selectedRow, 0) as File?
            } else null
        }

        this.addMouseListener(mouseListener)

        this.addSelectionListener()
        this.addFileRenderer()
        this.addDropTarget()
    }

    private fun addSelectionListener() {
        this.selectionModel.addListSelectionListener {
            if (!it.valueIsAdjusting) {
                if (this.selectedRow >= 0) {
                    val value = this.getValueAt(
                        this.selectedRow, 0
                    )

                    if (value is File &&
                        value.exists() &&
                        value.isFile &&
                        !value.isHidden
                    ) {
                        FilePanel.state(true)
                        EventSelectFile.trigger(value)
                    }
                } else {
                    FilePanel.state(false)
                }
            }
        }
    }

    private fun addFileRenderer() {
        this.setDefaultRenderer(
            File::class.java,
            object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): java.awt.Component = super.getTableCellRendererComponent(
                    table, (value as File).nameWithoutExtension,
                    isSelected, hasFocus,
                    row, column
                )
            }
        )
    }

    private fun addDropTarget() {
        this.dropTarget = object : DropTarget() {
            override fun dragEnter(dtde: DropTargetDragEvent) {
                if (Quiver.packDirectory == null) {
                    dtde.rejectDrag()
                }
            }

            override fun drop(dtde: DropTargetDropEvent) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE)
                @Suppress("UNCHECKED_CAST")
                val files = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>

                for (i in files) {
                    if (i.isFile) {
                        FileUtils.moveFileToDirectory(
                            i, Quiver.selectedDir, true
                        )

                        val file = File(Quiver.selectedDir, i.name)

                        // EventCreateFile.trigger(file)
                        // EventSelectFolder.trigger(Quiver.selectedDir!!)
                        // EventSearchFile.trigger(file)
                    }
                }
            }
        }
    }

    fun refreshAll() {
        this.removeAll()

        val document = Quiver.packDirectory
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

        this.setSortOrder(0, SortOrder.ASCENDING)
    }
}
