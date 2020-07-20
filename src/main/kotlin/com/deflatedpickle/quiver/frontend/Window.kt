package com.deflatedpickle.quiver.frontend

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import java.awt.Component
import javax.swing.Icon
import javax.swing.JFrame
import javax.swing.JScrollPane

object Window : JFrame("Quiver") {
    val control = CControl(this)
    val grid = CGrid(control)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(this.control.contentArea)

        this.pack()

        this.addDockable(
            "Folders", null, FolderTree,
            0.0, 0.0, 0.4, 1.0
        )

        this.addDockable(
            "Files", null, FileTable,
            1.0, 0.0, 1.0, 1.0
        )

        this.addDockable(
            "Properties", null, FilePanel,
            2.0, 0.0, 0.6, 1.0
        )
    }

    private fun addDockable(
        name: String, icon: Icon?, component: Component,
        x: Double, y: Double,
        width: Double, height: Double
    ) {
        val dockable = DefaultSingleCDockable(name, icon, name, JScrollPane(component))
        control.addDockable(dockable)
        dockable.isVisible = true
        this.grid.add(x, y, width, height, dockable)
    }
}