/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.menu

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventDeleteFile
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.backend.extension.toAsset
import com.deflatedpickle.quiver.frontend.dialog.UsagesDialog
import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.frontend.extension.disableAll
import com.jidesoft.swing.FolderChooser
import cr.nate.TransferableImage
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JPopupMenu
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.oxbow.swingbits.dialog.task.TaskDialogs

class FilePopupMenu(
    val getFile: () -> File?
) : JPopupMenu() {
    private val openItem = this.add("Open") {
        Desktop.getDesktop().open(this.getFile())
    }

    private val editItem = this.add("Edit") {
        try {
            Desktop.getDesktop().edit(this.getFile())
        } catch (e: IOException) {
            Desktop.getDesktop().open(this.getFile())
        }
    }.also {
        this.addSeparator()
    }

    private val cutItem = this.add("Cut") {
        this.getFile()?.let {
            copyContent(it)
            it.deleteRecursively()
            EventDeleteFile.trigger(it)
        }
    }

    private val copyItem = JMenu("Copy").also { this.add(it) }
    private val copyName = copyItem.add("Name") {
        this.getFile()?.nameWithoutExtension?.let { copyText(it) }
    }
    private val copyAsset = copyItem.add("Asset") {
        this.getFile()?.let {
            copyText(it.toAsset())
        }
    }
    private val copyPathItem = JMenu("Path").also { copyItem.add(it) }
    private val copyFilePath = copyPathItem.add("File") {
        this.getFile()?.absolutePath?.let { copyText(it) }
    }
    private val copyDirectoryPath = copyPathItem.add("Directory") {
        this.getFile()?.parentFile?.absolutePath?.let { copyText(it) }
    }

    private val copyContent = copyItem.add("Content") {
        this.getFile()?.let { copyContent(it) }
    }

    private val copySizeItem = JMenu("Size").also { copyItem.add(it) }
    private val copyWidth = copySizeItem.add("Width") {
        this.getFile()?.let { copyText(ImageIO.read(it).width.toString()) }
    }
    private val copyHeight = copySizeItem.add("Height") {
        this.getFile()?.let { copyText(ImageIO.read(it).height.toString()) }
    }

    private val pasteItem = this.add("Paste") {
        Quiver.selectedDir?.let { dir ->
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard

            if (clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
                val data = clipboard.getData(DataFlavor.javaFileListFlavor) as List<File>

                for (f in data) {
                    val newFile = dir.resolve(f.name)
                    FileUtils.copyFile(f, newFile)

                    EventSelectFolder.trigger(newFile.parentFile)
                    EventSelectFile.trigger(newFile)
                    EventSearchFile.trigger(newFile)
                }
            }
        }
    }

    private val deleteItem = this.add("Delete") {
        this.getFile()?.let {
            it.deleteRecursively()
            EventDeleteFile.trigger(it)
        }
    }.also {
        this.addSeparator()
    }

    private val renameItem = this.add("Rename") {
        this.getFile()?.let { file ->
            // This function can return null
            @Suppress("UNNECESSARY_SAFE_CALL")
            TaskDialogs.input(
                PluginUtil.window,
                "Rename File",
                "Choose a name to rename the file to",
                file.name
            )?.let { name ->
                val newFile = file.parentFile.resolve(name)
                file.renameTo(newFile)

                EventSelectFolder.trigger(newFile.parentFile)
                EventSelectFile.trigger(newFile)
                EventSearchFile.trigger(newFile)
            }
        }
    }

    private val moveItem = this.add("Move") {
        this.getFile()?.let { file ->
            val directoryChooser = FolderChooser(
                Quiver.selectedDir!!.absolutePath
            ).apply {
                dialogTitle = "Move File"
                recentList = listOf(Quiver.selectedDir!!.absolutePath)
                isFileHidingEnabled = false
            }
            val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

            if (openResult == JFileChooser.APPROVE_OPTION) {
                FileUtils.moveToDirectory(file, directoryChooser.selectedFolder, true)
                val newFile = directoryChooser.selectedFolder.resolve(file.name)

                EventSelectFolder.trigger(directoryChooser.selectedFile)
                EventSelectFile.trigger(newFile)
                EventSearchFile.trigger(newFile)
            }
        }
    }

    private val replaceItem = this.add("Replace") {
        this.getFile()?.let { file ->
            val directoryChooser = JFileChooser(
                Quiver.selectedDir!!.absolutePath
            ).apply {
                dialogTitle = "Replace File"
                fileSelectionMode = JFileChooser.FILES_ONLY
                isAcceptAllFileFilterUsed = false
            }
            val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

            if (openResult == JFileChooser.APPROVE_OPTION) {
                FileUtils.copyFile(directoryChooser.selectedFile, file)

                EventSelectFolder.trigger(file.parentFile)
                EventSelectFile.trigger(file)
                EventSearchFile.trigger(file)
            }
        }
    }.also {
        this.addSeparator()
    }

    private val findUsages = this.add("Find Usages") {
        this.getFile()?.let { file ->
            val dialog = UsagesDialog()

            GlobalScope.launch {
                dialog.refreshAll(file)
            }

            dialog.isVisible = true
        }
    }

    override fun setVisible(b: Boolean) {
        // Disable everything before we validate
        this.disableAll()

        if (Quiver.packDirectory != null && Quiver.selectedDir != null) {
            this.pasteItem.isEnabled = true
        }

        val file = this.getFile()

        // Disable some parts if they're a file or directory
        if (file != null) {
            this.openItem.isEnabled = true
            this.copyItem.isEnabled = true
            this.copyName.isEnabled = true
            this.copyPathItem.isEnabled = true
            this.copyFilePath.isEnabled = true
            this.copyDirectoryPath.isEnabled = true
            this.deleteItem.isEnabled = true
            this.moveItem.isEnabled = true

            if (file.isFile) {
                this.editItem.isEnabled = true
                this.copyAsset.isEnabled = true
                this.replaceItem.isEnabled = true
                this.renameItem.isEnabled = true
                this.findUsages.isEnabled = true
                this.copyContent.isEnabled = true
                this.cutItem.isEnabled = true

                when (file.extension) {
                    "png", "jpg", "jpeg" -> {
                        this.copySizeItem.isEnabled = true
                        this.copyWidth.isEnabled = true
                        this.copyHeight.isEnabled = true
                    }
                }
            }
        }

        super.setVisible(b)
    }

    private fun copyContent(it: File) {
        when (it.extension) {
            "png", "jpg", "jpeg" -> copyImage(ImageIO.read(it))
            else -> copyText(it.readText())
        }
    }

    private fun copy(transferable: Transferable) {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(
                transferable,
                null
            )
    }

    private fun copyText(string: String) = this.copy(StringSelection(string))
    private fun copyImage(image: BufferedImage) = this.copy(TransferableImage(image))
}
