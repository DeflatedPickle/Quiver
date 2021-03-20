/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.extension

import java.awt.event.ActionEvent
import javax.swing.Icon
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

fun JPopupMenu.add(text: String, icon: Icon? = null, action: (ActionEvent) -> Unit): JMenuItem {
    val item = JMenuItem(text, icon).apply { addActionListener { action(it) } }
    this.add(item)
    return item
}

fun JPopupMenu.disableAll() {
    for (i in this.components) {
        if (i is JMenu) {
            i.disableAll()
        } else if (i is JPopupMenu) {
            i.disableAll()
        }

        i.isEnabled = false
    }
}
