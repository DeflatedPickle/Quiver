package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.event.EventCreateFile
import com.deflatedpickle.quiver.frontend.dialog.NewDialog
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.io.File

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
}