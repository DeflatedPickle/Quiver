/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.extension

import java.awt.Component
import javax.swing.JToolBar
import org.jdesktop.swingx.JXButton
import javax.swing.Icon

fun JToolBar.add(text: String, action: () -> Unit): JXButton =
    JXButton(text).apply {
        addActionListener { action() }
        this@add.add(this)
    }

fun JToolBar.add(icon: Icon, tooltip: String = "", action: () -> Unit): JXButton =
    JXButton(icon).apply {
        if (tooltip.isNotEmpty()) {
            toolTipText = tooltip
        }
        addActionListener { action() }
        this@add.add(this)
    }
