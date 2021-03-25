/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventReplaceFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.JToolBar
import javax.swing.SwingUtilities
import org.apache.commons.io.FileUtils
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXRadioGroup

@Suppress("unused")
@Plugin(
    value = "file_panel",
    author = "DeflatedPickle",
    version = "1.1.1",
    description = """
        <br>
        Provides a panel on which a given file can be configured
    """,
    type = PluginType.COMPONENT,
    component = FilePanel::class
)
object FilePanelPlugin {
    private val radioButtonGroup = JXRadioGroup<String>()
    private val viewerToolbar = JToolBar("Viewer").apply { add(radioButtonGroup) }

    init {
        @Suppress("UNCHECKED_CAST")
        RegistryUtil.register("viewer", Registry<String, MutableList<Viewer<Any>>>() as Registry<String, Any>)

        EventProgramFinishSetup.addListener {
            FilePanel.widgetPanel.add(this.viewerToolbar, BorderLayout.NORTH)
        }

        EventSelectFile.addListener { it ->
            FilePanel.nameField.text = it.nameWithoutExtension
            FilePanel.typeField.text = it.extension

            FilePanel.fileSize.text = FileUtils.byteCountToDisplaySize(it.length())

            for (component in FilePanel.widgetPanel.components) {
                // Remove everything but the toolbar to change viewers
                if (component != this.viewerToolbar) {
                    FilePanel.widgetPanel.remove(component)
                }
            }
            radioButtonGroup.setValues(arrayOf())

            val registry = RegistryUtil.get("viewer")

            val viewerList = registry?.get(it.extension) as MutableList<Viewer<Any>>?

            // If there there are viewers for this extension...
            if (viewerList != null) {
                for (viewer in viewerList) {
                    // Add a button to switch to it
                    radioButtonGroup.add(viewer::class.simpleName)
                    // Get that button and listen to clicks, to set
                    radioButtonGroup.getChildButton(viewer::class.simpleName).addActionListener { _ ->
                        for (component in FilePanel.widgetPanel.components) {
                            if (component !is JToolBar) {
                                FilePanel.widgetPanel.remove(component)
                            }
                        }
                        EventChangeViewWidget.trigger(Pair(it, FilePanel.widgetPanel))

                        // Refresh the content in the viewer
                        SwingUtilities.invokeLater {
                            viewer.refresh(it)
                        }
                        // Add the viewer wrapped by it's scroller
                        FilePanel.widgetPanel.add(JXPanel(BorderLayout()).apply {
                            add(viewer.getScroller() ?: viewer.getComponent(), BorderLayout.CENTER)

                            viewer.getToolBars()?.let { bar ->
                                bar.north?.let { add(it, BorderLayout.NORTH) }
                                bar.east?.let { add(it, BorderLayout.EAST) }
                                bar.south?.let { add(it, BorderLayout.SOUTH) }
                                bar.west?.let { add(it, BorderLayout.WEST) }
                            }
                        }, BorderLayout.CENTER)

                        // We added the viewer, so we have to repaint it
                        FilePanel.widgetPanel.repaint()
                        FilePanel.widgetPanel.revalidate()
                    }
                }
            }

            if (radioButtonGroup.childButtonCount > 0) {
                // This selects the first viewer
                radioButtonGroup
                    .getChildButton(0).apply { isSelected = true }
                    .actionListeners
                    .first()
                    // The action isn't performed when we select it (silly, right?)
                    // So we have to send out an event for it
                    .actionPerformed(
                        ActionEvent(
                            this,
                            ActionEvent.ACTION_PERFORMED,
                            null
                        )
                    )
            }

            // Radio buttons we're added/removed, we need to repaint
            radioButtonGroup.repaint()
            radioButtonGroup.revalidate()

            // We'll also repaint this
            FilePanel.widgetPanel.repaint()
            FilePanel.widgetPanel.revalidate()
        }

        EventReplaceFile.addListener {
            EventSelectFile.trigger(it)
        }
    }
}
