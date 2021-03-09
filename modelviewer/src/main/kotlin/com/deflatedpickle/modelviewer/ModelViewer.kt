package com.deflatedpickle.modelviewer

import com.deflatedpickle.quiver.backend.api.Viewer
import java.awt.Component
import java.io.File
import javax.swing.JScrollPane

object ModelViewer : Viewer<File> {
    private val component = ModelViewerComponent()

    override fun refresh(with: File) {
        this.component.paintGL()
    }

    override fun getComponent(): Component = this.component
    override fun getScroller(): JScrollPane = JScrollPane(this.getComponent())
}