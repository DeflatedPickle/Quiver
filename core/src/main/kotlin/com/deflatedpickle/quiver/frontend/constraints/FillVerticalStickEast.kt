/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints

object FillVerticalStickEast : GridBagConstraints() {
    init {
        anchor = EAST
        fill = VERTICAL
        weighty = 1.0
    }
}
