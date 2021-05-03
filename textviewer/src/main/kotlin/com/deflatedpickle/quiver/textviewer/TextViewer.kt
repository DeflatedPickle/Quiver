/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.quiver.filepanel.api.Viewer
import com.deflatedpickle.quiver.backend.extension.toSyntaxEditingStyle
import java.io.File
import javax.swing.JComponent
import javax.swing.JScrollPane
import org.fife.ui.rtextarea.RTextScrollPane

object TextViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        this.component.text = with.readText()

        this.component.syntaxEditingStyle = with.extension.toSyntaxEditingStyle()
    }

    override fun getComponent(): JComponent = this.component
    override fun getScroller(): JScrollPane = RTextScrollPane(this.getComponent())
}
