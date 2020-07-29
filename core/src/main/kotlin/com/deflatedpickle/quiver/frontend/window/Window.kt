package com.deflatedpickle.quiver.frontend.window

import bibliothek.gui.dock.common.CControl
import bibliothek.gui.dock.common.CGrid
import java.awt.BorderLayout
import javax.swing.JFrame

object Window : JFrame("Quiver") {
    val control = CControl(this)
    val grid = CGrid(control)

    init {
        this.defaultCloseOperation = EXIT_ON_CLOSE

        this.add(Toolbar, BorderLayout.PAGE_START)
        this.add(control.contentArea, BorderLayout.CENTER)

        this.pack()
    }
}