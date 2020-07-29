package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints
import java.awt.Insets

object FillHorizontalFinishLine : GridBagConstraints() {
    init {
        anchor = NORTH
        fill = HORIZONTAL
        weightx = 1.0
        gridwidth = REMAINDER
        insets = Insets(2, 2, 2, 2)
    }
}
