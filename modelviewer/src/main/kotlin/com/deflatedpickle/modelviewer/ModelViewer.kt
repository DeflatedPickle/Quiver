package com.deflatedpickle.modelviewer

import com.deflatedpickle.quiver.backend.api.Viewer
import java.io.File
import javax.swing.SwingUtilities
import org.lwjgl.opengl.awt.AWTGLCanvas

object ModelViewer : Viewer<File> {
    private val component = ModelViewerComponent()

    override fun refresh(with: File) {
        val renderLoop: Runnable = object : Runnable {
            override fun run() {
                if (!component.isValid) return
                component.render()
                SwingUtilities.invokeLater(this)
            }
        }
        SwingUtilities.invokeLater(renderLoop)
    }

    override fun getComponent(): AWTGLCanvas = this.component
}