/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.frontend.dialog.NewDialog
import java.io.File
import javax.swing.JFileChooser
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs

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
            }/${dialog.nameEntry.text}"

            Quiver.packDirectory = File(path).apply {
                mkdirs()
                createNewFile()
            }

            when (dialog.packTypeGroup.selectedValue!!) {
                PackType.EMPTY_PACK -> {
                    PackUtil.createEmptyPack(path)

                    PackUtil.writeMcMeta(
                        dialog.packVersionComboBox.selectedItem as Int,
                        dialog.descriptionEntry.text
                    )
                }
                PackType.DEFAULT_PACK -> {
                    val file = dialog.defaultVersionComboBox.selectedItem as File

                    PackUtil.extractPack(
                        file.resolve("${file.name}.jar"),
                        path
                    )

                    val extraResourceTypes = mutableListOf<ExtraResourceType>()

                    val selectionModel = dialog.extraResourceTree.checkBoxListSelectionModel
                    for (row in 0..dialog.extraResourceTree.model.size) {
                        if (selectionModel.isSelectedIndex(row)) {
                            extraResourceTypes.add(ExtraResourceType.values()[row])
                        }
                    }

                    PackUtil.extractExtraData(
                        file.resolve("${file.name}.json"),
                        path,
                        *extraResourceTypes.toTypedArray()
                    )

                    PackUtil.writeMcMeta(
                        PackUtil.gameVersionToPackVersion(Version.fromString((dialog.defaultVersionComboBox.selectedItem as File).name)),
                        dialog.descriptionEntry.text
                    )
                }
            }

            EventNewDocument.trigger(Quiver.packDirectory!!)
        }
    }

    fun openPack() {
        val directoryChooser = JFileChooser(
            DotMinecraft.dotMinecraft.resolve("resourcepacks").absolutePath
        ).apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            val selected = directoryChooser.selectedFile

            if (selected.isDirectory &&
                selected.resolve("pack.mcmeta").isFile
            ) {
                Quiver.packDirectory = directoryChooser.selectedFile

                EventNewDocument.trigger(Quiver.packDirectory!!)
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
