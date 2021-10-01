/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.backend.util.PackUtil.findResolution
import org.oxbow.swingbits.dialog.task.TaskDialogs
import javax.swing.JFileChooser

object ActionUtil {
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
                Quiver.resolution = findResolution()
                Quiver.format = Quiver.json
                    .load(selected.resolve("pack.mcmeta"))
                    .getObject("pack")!!
                    .get(Int::class.java, "pack_format") as PackFormat

                EventNewDocument.trigger(Quiver.packDirectory!!)
                EventOpenPack.trigger(Quiver.packDirectory!!)
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
