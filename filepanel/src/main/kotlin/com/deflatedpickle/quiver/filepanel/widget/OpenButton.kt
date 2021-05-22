/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.widget

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.filepanel.FilePanelPlugin
import com.deflatedpickle.quiver.filepanel.api.Program
import com.deflatedpickle.quiver.filepanel.config.FilePanelSettings
import com.deflatedpickle.quiver.filepanel.dialog.AssignProgramDialog
import com.deflatedpickle.undulation.extensions.add
import kotlinx.serialization.InternalSerializationApi
import java.io.File
import javax.swing.JMenu
import javax.swing.JPopupMenu

@OptIn(InternalSerializationApi::class)
class OpenButton(
    enabled: Boolean,
    val open: () -> Unit,
    val openFolder: () -> Unit,
) : JSplitButton("  ", NagatoIcon.FOLDER_OPEN_FILE) {
    init {
        toolTipText = "Open File"
        isEnabled = enabled

        addButtonClickedActionListener {
            open()
        }
    }

    fun regenMenu() {
        popupMenu = JPopupMenu("Open Alternatives").apply {
            add(
                JMenu("Open In").also { menu ->
                    (FilePanelPlugin.programRegistry).get(Quiver.selectedFile!!.extension)?.apply {
                        for (program in this) {
                            addProgramExecutor(program, menu)
                        }
                    }
                    menu.addSeparator()
                    menu.add("Add Application") {
                        AssignProgramDialog.open(PluginUtil.window)?.let { program ->
                            ConfigUtil.getSettings<FilePanelSettings>("deflatedpickle@file_panel#>=1.0.0")
                                ?.let { settings ->
                                    settings.linkedPrograms.add(program)
                                    FilePanelPlugin.putProgramToRegistry(program)

                                    val id = PluginUtil.pluginToSlug(
                                        PluginUtil.slugToPlugin("deflatedpickle@file_panel#")!!
                                    )
                                    ConfigUtil.serializeConfig(
                                        id, File("config/$id.json")
                                    )

                                    if (Quiver.selectedFile!!.extension in program.extensions) {
                                        addProgramExecutor(program, menu)
                                    }
                                }
                        }
                    }
                }
            )
            addSeparator()
            add("Open Folder", NagatoIcon.FOLDER_OPEN) { openFolder() }
        }
    }

    private fun addProgramExecutor(program: Program, menu: JMenu) {
        menu.add(program.name, index = 0) {
            ProcessBuilder(
                program.command,
                program.args
                    .replace("{file}", Quiver.selectedFile!!.absolutePath)
                    .replace("{dir}", Quiver.selectedDir!!.absolutePath)
            )
                .directory(File(program.location).absoluteFile)
                .inheritIO()
                .start()
        }
    }
}
