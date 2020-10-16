package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.backend.event.EventOpenFile
import com.deflatedpickle.quiver.frontend.dialog.NewDialog
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.io.File
import javax.swing.JFileChooser

object ActionUtil {
    fun newPack() {
        val dialog = NewDialog()
        dialog.isVisible = true

        if (dialog.result == TaskDialog.StandardCommand.OK) {
            val path = "${
            if (dialog.locationEntry.field.text == "")
                System.getProperty("user.dir")
            else
                dialog.locationEntry.field.text
            }\\${dialog.nameEntry.text}"

            DocumentUtil.current = File(path).apply {
                mkdirs()
                createNewFile()
            }

            when (dialog.packTypeGroup.selectedValue!!) {
                PackType.EMPTY_PACK -> {
                    PackUtil.createEmptyPack(path, dialog.namespaceEntry.text)

                    PackUtil.writeMcMeta(
                        dialog.packVersionComboBox.selectedItem as Int,
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
                        PackUtil.gameVersionToPackVersion(Version.fromString((dialog.defaultVersionComboBox.selectedItem as File).name)),
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
        val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            val selected = directoryChooser.selectedFile

            if (selected.isDirectory &&
                    selected.resolve("pack.mcmeta").isFile) {
                DocumentUtil.current = directoryChooser.selectedFile

                EventOpenFile.trigger(DocumentUtil.current!!)
            } else {
                TaskDialogs.error(
                    PluginUtil.window,
                    "Invalid Pack",
                    "This directory is not a pack, as it does not contain a pack.mcmeta file"
                )
            }
        }
    }
}