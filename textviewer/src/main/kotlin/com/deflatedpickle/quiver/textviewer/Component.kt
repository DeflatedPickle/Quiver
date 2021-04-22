/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

class Component : RSyntaxTextArea() {
    init {
        this.isEditable = false

        this.antiAliasingEnabled = true
        this.isWhitespaceVisible = true
        this.paintTabLines = true
        this.isCodeFoldingEnabled = true
    }
}
