package com.deflatedpickle.quiver.launcher

import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.util.ActionUtil
import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.launcher.window.Toolbar
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
                add("New Pack") { ActionUtil.newPack() }
                add("Open Pack") { ActionUtil.openPack() }
                addSeparator()
            }

            Toolbar.add("New Pack") { ActionUtil.newPack() }
            Toolbar.add("Open Pack") { ActionUtil.openPack() }
        }
    }
}