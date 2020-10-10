package com.deflatedpickle.quiver.frontend.extension

import org.jdesktop.swingx.JXButton
import java.awt.Component
import javax.swing.JToolBar

fun JToolBar.add(text: String, action: () -> Unit): Component = this.add(JXButton(text).apply {
    addActionListener { action() }
})