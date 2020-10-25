/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.widget

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.backend.event.EventReplaceFile
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.filepanel.FilePanelPlugin
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXButton

class ReplaceButton(text: String) : JXButton(text) {
    init {
        this.addActionListener()
        this.addDropTarget()
    }

    private fun addActionListener() {
        this.addActionListener {
            val directoryChooser = JFileChooser().apply {
                fileSelectionMode = JFileChooser.FILES_ONLY
                isAcceptAllFileFilterUsed = false
                fileFilter = FileNameExtensionFilter("PNG files", "png")
            }
            val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

            if (openResult == JFileChooser.APPROVE_OPTION) {
                val selected = directoryChooser.selectedFile

                if (selected.isFile) {
                    FilePanelPlugin.selectedFile!!.delete()
                    // Replace the selected file with the dropped one
                    FileUtils.moveFile(selected, FilePanelPlugin.selectedFile)
                    EventReplaceFile.trigger(FilePanelPlugin.selectedFile!!)
                }
            }
        }
    }

    private fun addDropTarget() {
        this.dropTarget = object : DropTarget() {
            @Suppress("UNCHECKED_CAST")
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

            @Suppress("UNCHECKED_CAST")
            override fun drop(dtde: DropTargetDropEvent) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE)
                val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                val file = fileList[0]

                if (fileList[0].isFile && FilePanelPlugin.selectedFile != null) {
                    FilePanelPlugin.selectedFile!!.delete()
                    // Replace the selected file with the dropped one
                    FileUtils.moveFile(file, FilePanelPlugin.selectedFile)
                    EventReplaceFile.trigger(FilePanelPlugin.selectedFile!!)
                }
            }
        }
    }
}
