/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer.link

import org.apache.logging.log4j.LogManager
import org.fife.ui.rsyntaxtextarea.LinkGenerator
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea

object MinecraftLinkGenerator : LinkGenerator {
    private val logger = LogManager.getLogger()

    private val assetRegex = Regex("""((\w+:)(\w+/)*|(\w+/)+)(\w+)(\.\w+)?""")
    private val langRegex = Regex("""(\w+\.)+\w+(=\w+)?""")

    override fun isLinkAtOffset(
        textArea: RSyntaxTextArea,
        offs: Int
    ): LinkGeneratorResult? {
        if (offs < 0) return null

        val line = textArea.getLineOfOffset(offs)
        val first = textArea.getTokenListForLine(line)
        val doc = textArea.document as RSyntaxDocument

        var t = first
        while (t != null && t.isPaintable) {
            if (t.containsPosition(offs)) {
                val text = t.lexeme.replace("\"", "")
                // logger.debug("\"${t.lexeme}\" = Asset: ${text.matches(assetRegex)}, Lang: ${text.matches(langRegex)}")
                return when {
                    text.matches(assetRegex) -> AssetLinkGeneratorResult(textArea, t.offset, text)
                    text.matches(langRegex) -> LangLinkGeneratorResult(textArea, t.offset, text)
                    else -> null
                }
            }
            t = t.nextToken
        }

        return null
    }
}
