package com.deflatedpickle.quiver.backend.api

import javax.swing.JComponent
import javax.swing.JScrollPane

interface Viewer<T : Any> {
    fun refresh(with: T)

    fun getComponent(): JComponent
    fun getScroller(): JScrollPane
}