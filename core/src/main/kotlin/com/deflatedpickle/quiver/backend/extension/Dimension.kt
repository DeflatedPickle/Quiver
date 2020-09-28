package com.deflatedpickle.quiver.backend.extension

import java.awt.Dimension

operator fun Dimension.compareTo(size: Dimension): Int =
    if (this.width > size.width || this.height > size.height) {
        1
    } else if (this.width < size.width && this.height < size.height) {
        -1
    } else {
        0
    }