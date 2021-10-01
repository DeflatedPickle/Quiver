/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.packcreator

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.launcher.window.Toolbar
import com.deflatedpickle.quiver.packcreator.api.PackKind
import com.deflatedpickle.quiver.packcreator.dialog.NewDialog
import com.deflatedpickle.undulation.extensions.add
import javax.swing.JMenu

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A dialog to make new packs and an API to define how new packs are made
    """,
    type = PluginType.API
)
object PackCreatorPlugin {
    val packRegistry = Registry<String, PackKind>()

    init {
        EventProgramFinishSetup.addListener {
            RegistryUtil.register("pack", packRegistry)

            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New Pack", MonoIcon.FOLDER_NEW) { NewDialog.open() }
            }

            Toolbar.apply {
                add(icon = MonoIcon.FOLDER_NEW, tooltip = "New Pack") { NewDialog.open() }
            }
        }
    }
}
