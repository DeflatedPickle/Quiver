/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.extension

import java.awt.event.ActionEvent
import javax.swing.JMenu
import javax.swing.JMenuItem

fun JMenu.add(text: String, action: (ActionEvent) -> Unit): JMenuItem {
    val item = JMenuItem(text).apply { addActionListener { action(it) } }
    this.add(item)
    return item
}
