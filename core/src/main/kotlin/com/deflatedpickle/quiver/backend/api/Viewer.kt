package com.deflatedpickle.quiver.backend.api

import java.awt.Component
import javax.swing.JScrollPane

interface Viewer<T : Any> {
    fun refresh(with: T)

    fun getComponent(): Component
    fun getScroller(): JScrollPane
}