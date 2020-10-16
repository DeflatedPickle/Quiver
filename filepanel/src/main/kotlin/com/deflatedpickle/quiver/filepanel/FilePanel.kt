package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.lang.Lang
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.config.QuiverSettings
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
import com.deflatedpickle.quiver.filepanel.lang.FilePanelLang
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXRadioGroup
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.JToolBar

@Suppress("unused")
@Plugin(
    value = "file_panel",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        Provides a panel on which a given file can be configured
    """,
    type = PluginType.COMPONENT,
    component = Component::class
)
object FilePanel {
    var selectedFile: File? = null

    private val radioButtonGroup = JXRadioGroup<String>()
    private val viewerToolbar = JToolBar().apply { add(radioButtonGroup) }

    init {
        @Suppress("UNCHECKED_CAST")
        RegistryUtil.register("viewer", Registry<String, MutableList<Viewer<Any>>>() as Registry<String, Any>)

        EventProgramFinishSetup.addListener {
            this.viewerToolbar.name = FilePanelLang.trans("toolbar.viewer")
            Component.widgetPanel.add(this.viewerToolbar, BorderLayout.NORTH)
        }

        EventSelectFile.addListener {
            Component.nameField.text = it.nameWithoutExtension
            Component.typeField.text = it.extension

            Component.fileSize.text = FileUtils.byteCountToDisplaySize(it.length())

            this.selectedFile = it

            for (component in Component.widgetPanel.components) {
                if (component !is JToolBar) {
                    Component.widgetPanel.remove(component)
                }
            }
            radioButtonGroup.setValues(arrayOf())

            val registry = RegistryUtil.get("viewer")

            val viewerList = registry?.get(it.extension) as MutableList<Viewer<Any>>

            for (viewer in viewerList) {
                radioButtonGroup.add(viewer::class.simpleName)

                radioButtonGroup.getChildButton(viewer::class.simpleName).addActionListener { _ ->
                    for (component in Component.widgetPanel.components) {
                        if (component !is JToolBar) {
                            Component.widgetPanel.remove(component)
                        }
                    }
                    EventChangeViewWidget.trigger(Pair(it, Component.widgetPanel))

                    viewer.refresh(it)
                    Component.widgetPanel.add(viewer.getScroller(), BorderLayout.CENTER)

                    Component.widgetPanel.repaint()
                    Component.widgetPanel.revalidate()
                }
            }

            if (radioButtonGroup.childButtonCount > 0) {
                radioButtonGroup
                    .getChildButton(0).apply { isSelected = true }
                    .actionListeners
                    .first()
                        // The action isn't performed when we select it
                        // So we have to send out an event for it
                    .actionPerformed(
                        ActionEvent(
                            this,
                            ActionEvent.ACTION_PERFORMED,
                            null)
                    )
            }

            radioButtonGroup.repaint()
            radioButtonGroup.revalidate()

            Component.widgetPanel.repaint()
            Component.widgetPanel.revalidate()
        }
    }
}