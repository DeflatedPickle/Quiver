/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventMenuBuild
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.util.ActionUtil
import javax.swing.JMenu

@Suppress("unused", "SpellCheckingInspection")
@Plugin(
    value = "quiver",
    author = "DeflatedPickle",
    version = "1.2.0",
    description = """
        <br>
        A program for creating Minecraft resource-packs
    """,
    type = PluginType.CORE_API
)
object Quiver {
    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("New").apply { addActionListener { ActionUtil.newPack() } }
                add("Open").apply { addActionListener { ActionUtil.openPack() } }
                addSeparator()
            }
        }
    }
}
