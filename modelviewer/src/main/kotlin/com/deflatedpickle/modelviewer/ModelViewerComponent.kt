package com.deflatedpickle.modelviewer

import org.lwjgl.opengl.awt.AWTGLCanvas
import org.lwjgl.opengl.awt.GLData

class ModelViewerComponent : AWTGLCanvas(glData) {
    companion object {
        val glData = GLData()

        init {
            glData.samples = 4
        }
    }

    override fun initGL() {
    }

    override fun paintGL() {
    }
}