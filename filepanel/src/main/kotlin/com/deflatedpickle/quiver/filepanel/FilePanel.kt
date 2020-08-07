package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget

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
        EventSelectFile.addListener {
            Component.nameField.text = it.nameWithoutExtension
            Component.typeField.text = it.extension

            Component.widgetPanel.removeAll()
            EventChangeViewWidget.trigger(Pair(it, Component.widgetPanel))
        }
    }
}