/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer.link

import org.fife.ui.rsyntaxtextarea.LinkGenerator
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.Token

object AssetLinkGenerator : LinkGenerator {
    private val assetRegex = Regex(""""(?:(\w+):)?((?:\w+/?)+)"""")

    override fun isLinkAtOffset(
        textArea: RSyntaxTextArea,
        offs: Int
    ): LinkGeneratorResult? {
        if (offs < 0) return null

        val line = textArea.getLineOfOffset(offs)
        val first = textArea.getTokenListForLine(line)
        val doc = textArea.document as RSyntaxDocument

        var t: Token? = first
        while (t != null && t.isPaintable) {
            if (t.containsPosition(offs)) {
                if (t.lexeme.matches(assetRegex)) {
                    return AssetLinkGeneratorResult(textArea, t.offset, t.lexeme)
                }
            }
            t = t.nextToken
        }

        return null
    }
}
