/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.spritesheetviewer

import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.api.Viewer
import java.io.File
import java.lang.Integer.max
import java.lang.Integer.min
import javax.imageio.ImageIO
import javax.swing.JScrollPane

object SpriteSheetViewer : Viewer<File> {
    var index = 0
        set(value) {
            field = min(max(value, 0), maxIndex)
            SpriteSheetViewerPlugin.validateButtons()
        }
    var maxIndex = 0

    override fun refresh(with: File) {
        val image = ImageIO.read(with)

        if (image.width == Quiver.resolution) {
            SpriteSheetComponent.image = image.getSubimage(0, index * Quiver.resolution, Quiver.resolution, Quiver.resolution)
            maxIndex = (image.height / Quiver.resolution) - 1
            SpriteSheetViewerPlugin.indexLabel.text = "${index + 1}/${maxIndex + 1}"
            SpriteSheetViewerPlugin.validateButtons()
        } else {
            SpriteSheetComponent.image = image
        }

        SpriteSheetComponent.repaint()
    }

    override fun getComponent() = SpriteSheetComponent
    override fun getScroller() = JScrollPane(getComponent())
    override fun getToolBars() = Viewer.ToolBarPosition(
        north = SpriteSheetViewerPlugin.mediaToolbar,
        south = SpriteSheetViewerPlugin.toolbar
    )
}
