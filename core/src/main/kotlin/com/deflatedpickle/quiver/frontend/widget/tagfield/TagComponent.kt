package com.deflatedpickle.quiver.frontend.widget.tagfield

import org.jdesktop.swingx.HorizontalLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class TagComponent(private val text: String, private val closeable: Boolean = true) : JPanel() {
    init {
        this.layout = HorizontalLayout()
        this.border = BorderFactory.createEtchedBorder()

        this.add(JLabel(this.text))

        if (this.closeable) {
            this.add(JButton("X"))
        }
    }
}