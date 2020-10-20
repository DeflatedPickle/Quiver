package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.rawky.ui.constraints.*
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.GridBagLayout
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JComponent

object FilePanel : PluginPanel() {
    private val nameLabel = JXLabel("Name")
    val nameField = JXTextField("Name").apply { isEnabled = false }
    private val dotLabel = JXLabel(".")
    val typeField = JXTextField("Type").apply { isEnabled = false }

    private val fileSizeLabel = JXLabel("File Size")
    val fileSize = JXLabel()

    private val openButton = JXButton("Open").apply {
        isEnabled = false

        addActionListener {
            Desktop.getDesktop().open(FilePanelPlugin.selectedFile)
        }
    }

    private val editButton = JXButton("Edit").apply {
        isEnabled = false

        addActionListener {
            Desktop.getDesktop().edit(FilePanelPlugin.selectedFile)
        }
    }

    private val widgetArray = arrayOf<JComponent>(
        // nameField,
        // typeField,
        editButton,
        openButton
    )

    val widgetPanel = JXPanel().apply {
        border = BorderFactory.createTitledBorder("View")
        layout = BorderLayout()
    }

    init {
        this.layout = GridBagLayout()

        this.add(nameLabel, StickEast)
        this.add(nameField, FillHorizontal)
        this.add(dotLabel)
        this.add(typeField, FillHorizontalFinishLine)

        this.add(fileSizeLabel, StickEast)
        this.add(fileSize, FillHorizontalFinishLine)

        this.add(editButton, StickWest)
        this.add(openButton, StickWestFinishLine)

        this.add(widgetPanel, FillBothFinishLine)

        this.addDropTarget()
    }

    private fun addDropTarget() {
        this.dropTarget = object : DropTarget() {
            override fun dragEnter(dtde: DropTargetDragEvent) {
                val file = FilePanelPlugin.selectedFile
                val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>?

                if (DocumentUtil.current == null || file == null) {
                    dtde.rejectDrag()
                } else {
                    if (fileList != null && FilePanelPlugin.selectedFile != null) {
                        // We only want to allow one file
                        if (fileList.size != 1 && fileList[0].extension != file.extension) {
                            dtde.rejectDrag()
                        }
                    }
                }
            }

            override fun drop(dtde: DropTargetDropEvent) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE)
                val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                val file = fileList[0]

                if (fileList[0].isFile && FilePanelPlugin.selectedFile != null) {
                    FilePanelPlugin.selectedFile!!.delete()
                    // Replace the selected file with the dropped one
                    FileUtils.moveFile(file, FilePanelPlugin.selectedFile)
                    EventCreateFile.trigger(FilePanelPlugin.selectedFile!!)
                }
            }
        }
    }

    fun state(enabled: Boolean = true) {
        for (i in widgetArray) {
            i.isEnabled = enabled
        }
    }
}