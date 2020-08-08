package com.deflatedpickle.quiver.textviewer

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

object Viewer : RSyntaxTextArea() {
    init {
        this.isEnabled = false

        this.isCodeFoldingEnabled = true
    }
}