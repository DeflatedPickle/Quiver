/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

@file:Suppress("UNCHECKED_CAST")

package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.marvin.extensions.get
import com.deflatedpickle.marvin.extensions.set
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.filepanel.api.Viewer
import com.deflatedpickle.quiver.textviewer.api.Theme
import com.deflatedpickle.rawky.settings.SettingsGUI
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import java.awt.Component
import java.io.IOException
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.SwingUtilities
import org.fife.ui.rsyntaxtextarea.Theme as SyntaxTheme

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A viewer for text-based files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ],
    settings = TextViewerSettings::class
)
object TextViewerPlugin {
    private val extensionSet = setOf(
        "",
        "mcmeta",
        "txt",
        "json",
        "js", "ts",
        "gitignore",
        "bat", "py", "rb",
        "make", "makefile", "makef", "gmk", "mak",
        "gradle",
        "fsh", "vsh",
    )

    init {
        val themes = Reflections("org.fife.ui.rsyntaxtextarea.themes", ResourcesScanner()).getResources {
            it.split(".").last() == "xml"
        }

        EventOpenPack.addListener {
            if (Quiver.format <= 3) {
                (RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?)
                    ?.getOrRegister("lang", ::mutableListOf)?.let { it += TextViewer as Viewer<Any> }
            }
        }

        EventProgramFinishSetup.addListener {
            (RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?)?.let { registry ->
                for (i in this.extensionSet) {
                    registry.getOrRegister(i, ::mutableListOf)?.let { it += TextViewer as Viewer<Any> }
                }
            }

            ConfigUtil.getSettings<TextViewerSettings>("deflatedpickle@text_viewer#>=1.0.0")?.let { settings ->
                // Initially load the configured theme
                SwingUtilities.invokeLater {
                    try {
                        SyntaxTheme.load(
                            this::class.java.getResourceAsStream("/${settings.theme.id}")
                        ).apply(TextViewer.component)
                    } catch (e: IOException) {
                    }
                }

                // Add the setting widget to select a new theme
                (RegistryUtil.get("setting_type") as Registry<String, (Plugin, String, Any) -> Component>?)?.let { registry ->
                    registry.register(Theme::class.qualifiedName!!) { plugin, name, instance ->
                        JComboBox(themes.toTypedArray()).apply {
                            selectedItem = instance.get<Theme>(name).id

                            addActionListener {
                                instance.set(name, Theme(selectedItem as String))

                                SyntaxTheme.load(
                                    this::class.java.getResourceAsStream("/${settings.theme.id}")
                                ).apply(TextViewer.component)

                                SettingsGUI.serializeConfig(plugin)
                            }

                            setRenderer { list, value, index, isSelected, cellHasFocus ->
                                DefaultListCellRenderer().getListCellRendererComponent(
                                    list,
                                    value.split("/").last()
                                        .split(".").first()
                                        .capitalize(),
                                    index, isSelected, cellHasFocus
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
