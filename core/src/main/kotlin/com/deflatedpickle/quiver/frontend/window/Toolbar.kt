package com.deflatedpickle.quiver.frontend.window

import com.deflatedpickle.quiver.backend.util.ActionUtil
import org.jdesktop.swingx.JXButton
import javax.swing.JToolBar

object Toolbar : JToolBar() {
    val newButton = JXButton("New").apply {
        addActionListener {
            ActionUtil.newPack()
        }
    }

    init {
        this.add(newButton)
    }
}