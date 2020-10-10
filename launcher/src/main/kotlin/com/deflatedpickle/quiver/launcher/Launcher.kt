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
object Launcher {
    init {
        EventProgramFinishSetup.addListener {
            val lang = LangUtil.getLang("deflatedpickle@launcher#1.0.0")

            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add(lang.trans("action.new_pack")) { ActionUtil.newPack() }
                add(lang.trans("action.open_pack")) { ActionUtil.openPack() }
                addSeparator()
            }

            Toolbar.add(lang.trans("action.new_pack")) { ActionUtil.newPack() }
            Toolbar.add(lang.trans("action.open_pack")) { ActionUtil.openPack() }
        }
    }
}