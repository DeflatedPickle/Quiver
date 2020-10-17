package com.deflatedpickle.quiver.frontend.extension

import java.awt.event.ActionEvent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

fun JPopupMenu.add(text: String, action: (ActionEvent) -> Unit): JMenuItem {
    val item = JMenuItem(text).apply { addActionListener { action(it) } }
    this.add(item)
    return item
}