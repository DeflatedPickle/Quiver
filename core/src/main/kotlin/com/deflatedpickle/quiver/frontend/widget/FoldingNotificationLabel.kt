/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.jidesoft.dialog.BannerPanel
import javax.swing.BorderFactory
import javax.swing.JComponent
import org.jdesktop.swingx.JXCollapsiblePane

class FoldingNotificationLabel : JXCollapsiblePane() {
    val panel = BannerPanel().apply {
        border = BorderFactory.createEtchedBorder()
    }

    init {
        this.isCollapsed = true
        this.isAnimated = true
        this.add(panel)
    }

    fun setFields(title: String, subtitle: String, component: JComponent) {
        this.panel.title = title
        this.panel.subtitle = subtitle
        this.panel.iconComponent = component
    }

    fun emptyAndClose() {
        this.panel.title = ""
        this.panel.subtitle = ""
        this.panel.iconComponent = null

        this.isCollapsed = true
    }
}
