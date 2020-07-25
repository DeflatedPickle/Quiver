package com.deflatedpickle.quiver.frontend.window

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import bibliothek.gui.dock.common.DefaultSingleCDockable
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.frontend.FilePanel
import com.deflatedpickle.quiver.frontend.FileTable
import com.deflatedpickle.quiver.frontend.FolderTree
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.Icon
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JToolBar

object Window : JFrame("Quiver") {
    val control = CControl(this)
    val grid = CGrid(control)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(Toolbar, BorderLayout.PAGE_START)
        this.add(control.contentArea, BorderLayout.CENTER)

        this.pack()

        this.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                if (DocumentUtil.current != null) {
                    FolderTree.refreshAll()
                }
            }
        })

        addDockable(
            "Folders", null, FolderTree,
            0.0, 0.0, 0.4, 1.0
        )

        addDockable(
            "Files", null, FileTable,
            1.0, 0.0, 1.0, 1.0
        )

        addDockable(
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
        grid.add(x, y, width, height, dockable)
    }
}