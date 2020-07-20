package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints

object FillVerticalStickEast : GridBagConstraints() {
    init {
        anchor = EAST
        fill = VERTICAL
        weighty = 1.0
    }
}
