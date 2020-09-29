package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import org.apache.commons.io.FileUtils

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
    init {
        @Suppress("UNCHECKED_CAST")
        // TODO: Change to a list of viewers
        RegistryUtil.register("viewer", Registry<String, Viewer<Any>>() as Registry<String, Any>)

        EventSelectFile.addListener {
            Component.nameField.text = it.nameWithoutExtension
            Component.typeField.text = it.extension

            Component.fileSize.text = FileUtils.byteCountToDisplaySize(it.length())

            Component.widgetPanel.removeAll()
            EventChangeViewWidget.trigger(Pair(it, Component.widgetPanel))

            val registry = RegistryUtil.get("viewer")

            // TODO: Add a toolbar to switch between registered viewers
            val viewer = registry?.get(it.extension)

            if (viewer is Viewer<*>) {
                (viewer as Viewer<Any>).refresh(it)

                Component.widgetPanel.add(viewer.getScroller(), FillBothFinishLine)
            }
            Component.widgetPanel.repaint()
            Component.widgetPanel.revalidate()
        }
    }
}