/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.widget

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventReplaceFile
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXButton
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class ReplaceButton : JXButton(MonoIcon.REPLACE) {
    init {
        this.toolTipText = "Replace"

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
                    Quiver.selectedFile!!.delete()
                    // Replace the selected file with the dropped one
                    FileUtils.moveFile(selected, Quiver.selectedFile)
                    EventReplaceFile.trigger(Quiver.selectedFile!!)
                }
            }
        }
    }

    private fun addDropTarget() {
        this.dropTarget = object : DropTarget() {
            @Suppress("UNCHECKED_CAST")
            override fun dragEnter(dtde: DropTargetDragEvent) {
                val file = Quiver.selectedFile
                val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>?

                if (Quiver.packDirectory == null || file == null) {
                    dtde.rejectDrag()
                } else {
                    if (fileList != null &&
                        Quiver.selectedFile != null &&
                        fileList.size != 1 &&
                        fileList[0].extension != file.extension
                    ) {
                        // We only want to allow one file
                        dtde.rejectDrag()
                    }
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun drop(dtde: DropTargetDropEvent) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE)
                val fileList = dtde.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                val file = fileList[0]

                if (fileList[0].isFile && Quiver.selectedFile != null) {
                    Quiver.selectedFile!!.delete()
                    // Replace the selected file with the dropped one
                    FileUtils.moveFile(file, Quiver.selectedFile)
                    EventReplaceFile.trigger(Quiver.selectedFile!!)
                }
            }
        }
    }
}
