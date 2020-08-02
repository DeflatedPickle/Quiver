package com.deflatedpickle.quiver.frontend.window

import com.deflatedpickle.quiver.backend.util.ActionUtil
import org.jdesktop.swingx.JXButton
import javax.swing.JToolBar

object Toolbar : JToolBar() {
    private val buttons = mutableListOf(
        button("New") { ActionUtil.newPack() },
        button("Open") { ActionUtil.openPack() }
    )

    init {
        for (i in this.buttons) {
            this.add(i)
        }
    }

    private fun button(text: String, action: () -> Unit) = JXButton(text).apply {
        addActionListener { action() }
    }
}