/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.imageviewer

import com.deflatedpickle.quiver.backend.extension.compareTo
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.jdesktop.swingx.JXPanel
import so.madprogrammer.ScaleUtil.getScaleFactorToFit

class Component : JXPanel() {
    lateinit var image: BufferedImage

    // https://stackoverflow.com/a/11959928
    override fun paintComponent(g: Graphics) {
        val g2d = g as Graphics2D
        super.paintComponent(g)

        if (!this::image.isInitialized) return

        val imageDimension = Dimension(image.width, image.height)

        val scaleFactor = if (imageDimension > size) {
            min(1.0, getScaleFactorToFit(imageDimension, size))
        } else {
            max(1.0, getScaleFactorToFit(imageDimension, size))
        }

        val scaleWidth = (image.width * scaleFactor).roundToInt()
        val scaleHeight = (image.height * scaleFactor).roundToInt()

        val width = width - 1
        val height = height - 1

        val x = (width - scaleWidth) / 2
        val y = (height - scaleHeight) / 2

        g2d.drawImage(image, x, y, scaleWidth, scaleHeight, this)
    }
}
