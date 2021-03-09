/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.api

import java.awt.Component
import javax.swing.JScrollPane

interface Viewer<T : Any> {
    fun refresh(with: T)

    fun getComponent(): Component
    fun getScroller(): JScrollPane? = null

    fun usesScroller() = getScroller() != null
}
