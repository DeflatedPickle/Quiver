/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.backend.util.ActionUtil
import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.launcher.window.Toolbar
import javax.swing.JComponent
import javax.swing.JMenu

@Plugin(
    value = "launcher",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A basic launcher
    """,
    type = PluginType.LAUNCHER
)
@Suppress("unused")
object LauncherPlugin {
    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New Pack", NagatoIcon.FOLDER_NEW) { ActionUtil.newPack() }
                add("Open Pack", NagatoIcon.FOLDER_OPEN) { ActionUtil.openPack() }
                addSeparator()
            }

            Toolbar.add(NagatoIcon.FOLDER_NEW, "New Pack") { ActionUtil.newPack() } as JComponent
            Toolbar.add(NagatoIcon.FOLDER_OPEN, "Open Pack") { ActionUtil.openPack() }
        }
    }
}
