package com.deflatedpickle.quiver.launcher

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.util.ActionUtil
import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.launcher.lang.LauncherLang
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
object Launcher {
    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            if (menuBar != null) {
                val menuFile = menuBar.get(MenuCategory.FILE.name)
                if (menuFile is JMenu) {
                    menuFile.text = LauncherLang.trans("menu.file")

                    menuFile.add(LauncherLang.trans("action.new_pack")) { ActionUtil.newPack() }
                    menuFile.add(LauncherLang.trans("action.open_pack")) { ActionUtil.openPack() }
                    menuFile.addSeparator()
                }

                val menuTools = menuBar.get(MenuCategory.TOOLS.name)
                if (menuTools is JMenu) {
                    menuTools.text = LauncherLang.trans("menu.tools")
                }
            }

            Toolbar.add(LauncherLang.trans("action.new_pack")) { ActionUtil.newPack() }
            Toolbar.add(LauncherLang.trans("action.open_pack")) { ActionUtil.openPack() }
        }
    }
}