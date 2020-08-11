package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import javax.swing.JScrollPane

@Suppress("unused")
@Plugin(
    value = "tree_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for lang and properties files
    """,
    type = PluginType.OTHER
)
object TableViewer {
    private val extensionSet = setOf(
        "properties"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer")

            if (registry != null) {
                for (i in this.extensionSet) {
                    registry.register(i, Viewer)
                }
            }
        }
    }
}