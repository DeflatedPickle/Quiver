/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer.link

import com.deflatedpickle.quiver.translationmatrix.gui.TranslationMatrix
import kotlinx.serialization.ExperimentalSerializationApi
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.io.File
import javax.swing.event.HyperlinkEvent

class LangLinkGeneratorResult(
    private val textArea: RSyntaxTextArea,
    private val offset: Int,
    private val file: String
) : LinkGeneratorResult {
    private var searchedFile: File? = null

    @ExperimentalSerializationApi
    override fun execute(): HyperlinkEvent? {
        TranslationMatrix.openAt("")
        return null
    }

    override fun getSourceOffset(): Int = offset
}
