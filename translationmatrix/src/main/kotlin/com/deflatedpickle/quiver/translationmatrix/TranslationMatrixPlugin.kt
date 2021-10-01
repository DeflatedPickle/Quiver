/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.translationmatrix

import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.launcher.window.Toolbar
import com.deflatedpickle.quiver.translationmatrix.config.TranslationMatrixSettings
import com.deflatedpickle.quiver.translationmatrix.gui.TranslationMatrix
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.extensions.add
import org.jdesktop.swingx.JXButton
import javax.swing.JMenu
import javax.swing.JMenuItem

@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A dialog for editing translations
    """,
    type = PluginType.OTHER,
    settings = TranslationMatrixSettings::class
)
// TODO: [TranslationMatrixPlugin] Add an editor for LangKey cells
object TranslationMatrixPlugin {
    private lateinit var toolbarButton: JXButton
    private lateinit var menuButton: JMenuItem

    init {
        EventProgramFinishSetup.addListener {
            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                menuButton = add("Translation Matrix", MonoIcon.TRANSLATE, enabled = false) { TranslationMatrix.open() }
                addSeparator()
            }

            Toolbar.apply {
                toolbarButton = add(icon = MonoIcon.TRANSLATE, tooltip = "Translation Matrix", enabled = false) { TranslationMatrix.open() }
            }
        }

        EventOpenPack.addListener {
            menuButton.isEnabled = true
            toolbarButton.isEnabled = true
        }

        EventChangeTheme.addListener {
        }
    }
}
