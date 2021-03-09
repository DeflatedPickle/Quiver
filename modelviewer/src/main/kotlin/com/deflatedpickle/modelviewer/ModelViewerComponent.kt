package com.deflatedpickle.modelviewer

import kotlin.math.abs
import kotlin.math.sin
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glBegin
import org.lwjgl.opengl.GL11.glColor3f
import org.lwjgl.opengl.GL11.glEnd
import org.lwjgl.opengl.GL11.glVertex2f
import org.lwjgl.opengl.awt.AWTGLCanvas
import org.lwjgl.opengl.awt.GLData


class ModelViewerComponent : AWTGLCanvas(glData) {
    companion object {
        val glData = GLData()

        init {
            glData.samples = 4
        }
    }

    private fun resizeGL() {
        GL11.glViewport(0, 0, this.width, this.height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
    }

    private fun clearGL() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
    }

    override fun initGL() {
        createCapabilities()
        resizeGL()
        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun paintGL() {
        this.clearGL()

        this.swapBuffers()
    }
}