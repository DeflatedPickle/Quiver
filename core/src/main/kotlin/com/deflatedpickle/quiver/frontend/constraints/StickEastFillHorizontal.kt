/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.rawky.ui.constraints

import java.awt.GridBagConstraints

object StickEastFillHorizontal : GridBagConstraints() {
    init {
        anchor = EAST
        fill = HORIZONTAL
        weightx = 1.0
    }
}
