/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.quiver.backend.extension.toSyntaxEditingStyle
import com.deflatedpickle.quiver.filepanel.api.Viewer
import com.deflatedpickle.quiver.textviewer.link.MinecraftLinkGenerator
import org.fife.ui.rsyntaxtextarea.ErrorStrip
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice
import org.fife.ui.rtextarea.RTextScrollPane
import org.jdesktop.swingx.JXPanel
import java.awt.BorderLayout
import java.awt.event.InputEvent
import java.io.File
import javax.swing.JComponent

object TextViewer : Viewer<File> {
    internal val component = RSyntaxTextArea().apply {
        isEditable = false
        isWhitespaceVisible = true
        isCodeFoldingEnabled = true

        markOccurrences = true
        paintTabLines = true

        antiAliasingEnabled = true
        hyperlinksEnabled = true

        linkGenerator = MinecraftLinkGenerator
        // We have to have a listener for the link generator to activate results
        addHyperlinkListener {}
    }

    private val errorStrip = ErrorStrip(this.component).apply {
        followCaret = true
        levelThreshold = ParserNotice.Level.ERROR
        showMarkAll = true
        showMarkedOccurrences = true
    }

    private val scrollPane = RTextScrollPane(this.component).apply {
        addMouseWheelListener { event ->
            when (event.modifiers) {
                InputEvent.CTRL_MASK -> {
                    if (event.wheelRotation < 0) {
                        RSyntaxTextAreaEditorKit.IncreaseFontSizeAction().actionPerformedImpl(null, component)
                    } else {
                        RSyntaxTextAreaEditorKit.DecreaseFontSizeAction().actionPerformedImpl(null, component)
                    }
                }
            }
        }
    }

    private val panel = JXPanel(BorderLayout()).apply {
        add(errorStrip, BorderLayout.LINE_START)
        add(scrollPane, BorderLayout.CENTER)
    }

    override fun refresh(with: File) {
        this.component.text = with.readText()

        this.component.caretPosition = 0
        this.component.syntaxEditingStyle = with.extension.toSyntaxEditingStyle()
    }

    override fun getComponent(): JComponent = this.panel
}
