/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.api

import java.awt.Component
import javax.swing.JScrollPane
import javax.swing.JToolBar

interface Viewer<T : Any> {
    data class ToolBarPosition(
        val north: JToolBar? = null,
        val east: JToolBar? = null,
        val south: JToolBar? = null,
        val west: JToolBar? = null
    )

    fun refresh(with: T)

    fun getComponent(): Component
    fun getScroller(): JScrollPane? = null
    fun getToolBars(): ToolBarPosition? = null
}
