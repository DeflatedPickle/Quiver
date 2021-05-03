/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.markdownviewer

import com.deflatedpickle.quiver.filepanel.api.Viewer
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane
import org.xhtmlrenderer.simple.FSScrollPane
import org.xhtmlrenderer.simple.XHTMLPanel

object MarkdownViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        val options = MutableDataSet()
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val document = parser.parse(with.readText())
        val html = renderer.render(document)

        val parent: File = with.absoluteFile.parentFile
        val parentURL = parent.toURI().toURL().toExternalForm()
        getComponent().setDocument("<html>$html</html>".byteInputStream(), parentURL)
    }

    override fun getComponent(): XHTMLPanel = this.component
    override fun getScroller(): JScrollPane = FSScrollPane(this.getComponent() as JPanel)
}
