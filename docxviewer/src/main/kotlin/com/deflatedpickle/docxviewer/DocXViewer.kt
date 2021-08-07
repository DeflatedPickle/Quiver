/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.docxviewer

import com.deflatedpickle.quiver.filepanel.api.Viewer
import org.xhtmlrenderer.simple.FSScrollPane
import org.xhtmlrenderer.simple.XHTMLPanel
import org.zwobble.mammoth.DocumentConverter
import java.io.File
import javax.swing.JPanel
import javax.swing.JScrollPane

object DocXViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        val converter = DocumentConverter()
        val result = converter.convertToHtml(with)
        val html = result.value

        for (i in result.warnings) {
            DocXViewerPlugin.logger.warn(i)
        }

        val parent: File = with.absoluteFile.parentFile
        val parentURL = parent.toURI().toURL().toExternalForm()
        getComponent().setDocument("<html>$html</html>".byteInputStream(), parentURL)
    }

    override fun getComponent(): XHTMLPanel = this.component
    override fun getScroller(): JScrollPane = FSScrollPane(this.getComponent() as JPanel)
}
