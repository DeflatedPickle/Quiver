package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
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
    private val viewerToolbar = JToolBar("Viewer").apply { add(radioButtonGroup) }

    init {
        @Suppress("UNCHECKED_CAST")
        RegistryUtil.register("viewer", Registry<String, MutableList<Viewer<Any>>>() as Registry<String, Any>)

        EventProgramFinishSetup.addListener {
            Component.widgetPanel.add(this.viewerToolbar, BorderLayout.NORTH)
        }

        EventSelectFile.addListener {
            Component.nameField.text = it.nameWithoutExtension
            Component.typeField.text = it.extension

            Component.fileSize.text = FileUtils.byteCountToDisplaySize(it.length())

            this.selectedFile = it

            for (component in Component.widgetPanel.components) {
                // Remove everything but the toolbar to change viewers
                if (component != this.viewerToolbar) {
                    Component.widgetPanel.remove(component)
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
                        for (component in Component.widgetPanel.components) {
                            if (component !is JToolBar) {
                                Component.widgetPanel.remove(component)
                            }
                        }
                        EventChangeViewWidget.trigger(Pair(it, Component.widgetPanel))

                        // Refresh the content in the viewer
                        viewer.refresh(it)
                        // Add the viewer wrapped by it's scroller
                        Component.widgetPanel.add(viewer.getScroller(), BorderLayout.CENTER)

                        // We added the viewer, so we have to repaint it
                        Component.widgetPanel.repaint()
                        Component.widgetPanel.revalidate()
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
            Component.widgetPanel.repaint()
            Component.widgetPanel.revalidate()
        }
    }
}