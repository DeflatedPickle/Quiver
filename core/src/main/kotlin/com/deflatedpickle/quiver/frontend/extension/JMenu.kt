/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.extension

import java.awt.event.ActionEvent
import javax.swing.Icon
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

fun JMenu.add(text: String, icon: Icon? = null, action: (ActionEvent) -> Unit): JMenuItem =
    JMenuItem(text, icon).apply {
        addActionListener { action(it) }
        this@add.add(this)
    }

fun JMenu.disableAll() {
    for (i in this.menuComponents) {
        if (i is JMenu) {
            i.disableAll()
        } else if (i is JPopupMenu) {
            i.disableAll()
        }

        i.isEnabled = false
    }
}
