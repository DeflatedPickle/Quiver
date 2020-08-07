package com.deflatedpickle.quiver.txtviewer

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

object Viewer : RSyntaxTextArea() {
    init {
        this.isEnabled = false

        this.isCodeFoldingEnabled = true
    }
}