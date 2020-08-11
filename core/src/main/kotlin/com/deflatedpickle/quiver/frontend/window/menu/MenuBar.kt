package com.deflatedpickle.quiver.frontend.window.menu

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.event.EventMenuBarAdd
import com.deflatedpickle.haruhi.event.EventMenuBarBuild
import com.deflatedpickle.haruhi.event.EventMenuBuild
import com.deflatedpickle.haruhi.util.RegistryUtil
import javax.swing.JMenu
import javax.swing.JMenuBar

object MenuBar : JMenuBar() {
    private val menuRegistry = object : Registry<String, JMenu>() {
        init {
            register(MenuCategory.FILE.name, addMenu(JMenu("File")))
            register(MenuCategory.TOOLS.name, addMenu(JMenu("Tools")))
        }
    }

    init {
        RegistryUtil.register(MenuCategory.MENU.name, menuRegistry as Registry<String, Any>)
        EventMenuBarBuild.trigger(this)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun addMenu(menu: JMenu) : JMenu {
        EventMenuBuild.trigger(menu)
        this.add(menu)
        EventMenuBarAdd.trigger(menu)

        return menu
    }
}