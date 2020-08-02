package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.frontend.dialog.NewDialog
import com.deflatedpickle.quiver.frontend.window.Window
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.File
import javax.swing.JFileChooser

object ActionUtil {
    fun newPack() {
        val dialog = NewDialog()
        dialog.isVisible = true

        if (dialog.result == TaskDialog.StandardCommand.OK) {
            val path = "${
            if (dialog.locationEntry.text == "")
                System.getProperty("user.dir")
            else
                dialog.locationEntry.text
            }\\${dialog.nameEntry.text}"

            DocumentUtil.current = File(path).apply {
                mkdirs()
                createNewFile()
            }

            when (dialog.packTypeGroup.selectedValue!!) {
                PackType.EMPTY_PACK -> {
                    PackUtil.createEmptyPack(path, dialog.namespaceEntry.text)

                    PackUtil.writeMcMeta(
                        dialog.packVersionEntry.selectedItem as PackVersion,
                        dialog.descriptionEntry.text
                    )
                }
                PackType.DEFAULT_PACK -> {
                    val file = dialog.defaultVersionComboBox.selectedItem as File
                    PackUtil.extractPack(
                        file.resolve("${file.name}.jar"),
                        path,
                        dialog.namespaceEntry.text
                    )

                    PackUtil.writeMcMeta(
                        dialog.defaultVersionComboBox.selectedItem as PackVersion,
                        dialog.descriptionEntry.text
                    )
                }
            }

            EventCreateFile.trigger(DocumentUtil.current!!)
        }
    }

    fun openPack() {
        val directoryChooser = JFileChooser().apply {
            currentDirectory = File(".")
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        val openResult = directoryChooser.showOpenDialog(Window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            DocumentUtil.current = directoryChooser.selectedFile

            EventOpenFile.trigger(DocumentUtil.current!!)
        }
    }
}