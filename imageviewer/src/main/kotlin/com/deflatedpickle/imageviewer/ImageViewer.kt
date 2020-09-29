package com.deflatedpickle.imageviewer

import com.deflatedpickle.quiver.backend.api.Viewer
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JScrollPane


object ImageViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        component.image = ImageIO.read(with)
        component.repaint()
    }

    override fun getComponent(): JComponent = this.component
    override fun getScroller(): JScrollPane = JScrollPane(this.getComponent())
}