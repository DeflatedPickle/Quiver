/* Copyright (c) 2021 DeflatedPickle under the MIT license */

@file:Suppress("FunctionName")

package com.deflatedpickle.quiver.frontend.widget

import javax.swing.JComponent
import javax.swing.UIManager
import net.java.balloontip.BalloonTip
import net.java.balloontip.styles.RoundedBalloonStyle
import org.jdesktop.swingx.JXLabel

fun ThemedBalloonTip(on: JComponent, using: JComponent, close: Boolean = false, initiallyVisible: Boolean = false) =
    BalloonTip(
        on,
        using,
        RoundedBalloonStyle(
            5,
            5,
            UIManager.getColor("Panel.background"),
            UIManager.getColor("TitledBorder.titleColor")
        ),
        close
    ).apply {
        isVisible = initiallyVisible
    }

fun ThemedBalloonTip(on: JComponent, using: String, close: Boolean = false, initiallyVisible: Boolean = false) =
    ThemedBalloonTip(
        on,
        JXLabel(using),
        close,
        initiallyVisible
    )
