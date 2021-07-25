/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.backend.util.PackUtil.findResolution
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
            }/${dialog.nameEntry.text}"

            Quiver.packDirectory = File(path).apply {
                mkdirs()
                createNewFile()
            }

            when (dialog.packTypeGroup.selectedValue!!) {
                PackType.EMPTY_PACK -> {
                    fun isSelected(path: String) = dialog.folderStructure.checkBoxTreeSelectionModel.selectionPaths.filter {
                            dialog.folderStructure.checkBoxTreeSelectionModel.isPathSelected(it,
                                true)
                        }.map {
                            it.path.joinToString("/").replace("root/", "")
                        }.contains(path)

                    PackUtil.createEmptyPack(
                        path, true,
                        PackUtil.PackStructure(
                            packMcMeta = /*isSelected("pack.mcmeta")*/ true,
                            assets = if (isSelected("assets")) PackUtil.PackStructure.Assets(
                                icons = isSelected("assets/icons"),
                                minecraft = if (isSelected("assets/minecraft")) PackUtil.PackStructure.Minecraft(
                                    soundsJson = isSelected("assets/minecraft/sounds.json"),
                                    blockstates = isSelected("assets/minecraft/blockstates"),
                                    gpuWarnlistJson = isSelected("assets/minecraft/gpu_warnlist.json"),
                                    font = isSelected("assets/minecraft/font"),
                                    icons = isSelected("assets/minecraft/icons"),
                                    lang = isSelected("assets/minecraft/lang"),
                                    models = if (isSelected("assets/minecraft/models")) PackUtil.PackStructure.Minecraft.Models(
                                        block = isSelected("assets/minecraft/models/block"),
                                        item = isSelected("assets/minecraft/models/item")
                                    ) else null,
                                    particles = isSelected("assets/minecraft/particles"),
                                    sounds = isSelected("assets/minecraft/sounds"),
                                    shaders = if (isSelected("assets/minecraft/shaders")) PackUtil.PackStructure.Minecraft.Shaders(
                                        post = isSelected("assets/minecraft/shaders/post"),
                                        program = isSelected("assets/minecraft/shaders/program")
                                    ) else null,
                                    texts = isSelected("assets/minecraft/text"),
                                    textures = if (isSelected("assets/minecraft/textures")) PackUtil.PackStructure.Minecraft.Textures(
                                        block = isSelected("assets/minecraft/textures/block"),
                                        colourmap = isSelected("assets/minecraft/textures/colourmap"),
                                        effect = isSelected("assets/minecraft/textures/effect"),
                                        entity = isSelected("assets/minecraft/textures/entity"),
                                        environment = isSelected("assets/minecraft/textures/environment"),
                                        font = isSelected("assets/minecraft/textures/font"),
                                        gui = if (isSelected("assets/minecraft/textures/gui")) PackUtil.PackStructure.Minecraft.Textures.GUI(
                                            advancements = if (isSelected("assets/minecraft/textures/gui/advancements")) PackUtil.PackStructure.Minecraft.Textures.GUI.Advancements(
                                                backgrounds = isSelected("assets/minecraft/textures/gui/advancements/backgrounds")
                                            ) else null,
                                            container = if (isSelected("assets/minecraft/textures/gui/container")) PackUtil.PackStructure.Minecraft.Textures.GUI.Container(
                                                creativeInventory = isSelected("assets/minecraft/textures/gui/container/creative_inventory")
                                            ) else null,
                                            presets = isSelected("assets/minecraft/textures/gui/container/presets"),
                                            title = if (isSelected("assets/minecraft/textures/gui/title")) PackUtil.PackStructure.Minecraft.Textures.GUI.Title(
                                                background = isSelected("assets/minecraft/textures/gui/title/background")
                                            ) else null
                                        ) else null,
                                        item = isSelected("assets/minecraft/textures/item"),
                                        map = isSelected("assets/minecraft/textures/map"),
                                        misc = isSelected("assets/minecraft/textures/misc"),
                                        mobEffect = isSelected("assets/minecraft/textures/mob_effect"),
                                        models = if (isSelected("assets/minecraft/textures/models")) PackUtil.PackStructure.Minecraft.Textures.Models(
                                            armor = isSelected("assets/minecraft/textures/models/armor")
                                        ) else null,
                                        painting = isSelected("assets/minecraft/textures/painting"),
                                        particle = isSelected("assets/minecraft/textures/particle")
                                    ) else null
                                ) else null
                            ) else null
                        )
                    )

                    if (Quiver.packDirectory!!.resolve("pack.mcmeta").exists()) {
                        PackUtil.writeMcMeta(
                            dialog.packVersionComboBox.selectedItem as Int,
                            dialog.descriptionEntry.text
                        )
                    }
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

            for (f in dialog.postTaskQueue) {
                f()
            }

            when (dialog.packTypeGroup.selectedValue) {
                PackType.EMPTY_PACK -> Quiver.resolution = dialog.resolutionEntry.text.toInt()
                PackType.DEFAULT_PACK -> Quiver.resolution = findResolution()
            }

            EventNewDocument.trigger(Quiver.packDirectory!!)
            EventOpenPack.trigger(Quiver.packDirectory!!)
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
