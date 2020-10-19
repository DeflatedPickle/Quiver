package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.filepanel.Component
import com.deflatedpickle.quiver.frontend.menu.FilePopupMenu
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXTable
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

object Table : JXTable() {
    private val fileModel = object : DefaultTableModel(arrayOf(""), 0) {
        override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> File::class.java
                else -> String::class.java
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
            if (this.selectedRow >= 0)
                fileModel.getValueAt(this.selectedRow, 0) as File?
            else null
        }

        this.addSelectionListener()
        this.addFileRenderer()
        this.addDropTarget()
    }

    private fun addSelectionListener() {
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
    }

    private fun addFileRenderer() {
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

    private fun addDropTarget() {
        this.dropTarget = object : DropTarget() {
            override fun dragEnter(dtde: DropTargetDragEvent) {
                if (DocumentUtil.current == null) {
                    dtde.rejectDrag()
                }
            }

            override fun drop(dtde: DropTargetDropEvent) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE)
                val files = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>

                for (i in files) {
                    if (i.isFile) {
                        FileUtils.moveFileToDirectory(
                            i, FileTable.currentDir, true
                        )
                        EventCreateFile.trigger(File(FileTable.currentDir, i.name))
                    }
                }
            }
        }
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