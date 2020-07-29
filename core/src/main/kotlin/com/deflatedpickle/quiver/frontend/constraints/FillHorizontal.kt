package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints

object FillHorizontal : GridBagConstraints() {
    init {
        anchor = CENTER
        fill = HORIZONTAL
        weightx = 1.0
        gridwidth = 1
        gridheight = 1
    }
}
