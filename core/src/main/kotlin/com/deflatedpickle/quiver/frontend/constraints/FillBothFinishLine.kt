package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints
import java.awt.Insets

object FillBothFinishLine : GridBagConstraints() {
    init {
        anchor = NORTH
        fill = BOTH
        weightx = 1.0
        weighty = 1.0
        gridwidth = REMAINDER
        insets = Insets(2, 2, 2, 2)
    }
}
