/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.imageviewer

import com.deflatedpickle.quiver.filepanel.api.Viewer
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JScrollPane

object ImageViewer : Viewer<File> {
    private val component = ImageViewerComponent()

    override fun refresh(with: File) {
        val image = ImageIO.read(with)

        component.image = image
        component.repaint()
    }

    override fun getComponent() = component
    override fun getScroller() = JScrollPane(this.getComponent())
}
