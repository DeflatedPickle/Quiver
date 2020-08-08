package com.deflatedpickle.tableviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
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

    private val scroller = JScrollPane(Viewer)

    init {
        EventChangeViewWidget.addListener {
            if (it.first.extension in this.extensionSet) {
                Viewer.refresh(it.first)

                it.second.add(this.scroller, FillBothFinishLine)
                it.second.repaint()
                it.second.revalidate()
            }
        }
    }
}