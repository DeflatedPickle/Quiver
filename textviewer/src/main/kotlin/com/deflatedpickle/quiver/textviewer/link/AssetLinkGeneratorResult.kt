/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer.link

import com.deflatedpickle.marvin.extensions.div
import com.deflatedpickle.mmf.util.AssetPath
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import kotlinx.serialization.ExperimentalSerializationApi
import org.fife.ui.rsyntaxtextarea.LinkGeneratorResult
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import java.io.File
import javax.swing.event.HyperlinkEvent

class AssetLinkGeneratorResult(
    private val textArea: RSyntaxTextArea,
    private val offset: Int,
    private val file: String
) : LinkGeneratorResult {
    private var searchedFile: File? = null

    @ExperimentalSerializationApi
    override fun execute(): HyperlinkEvent? {
        val asset = AssetPath.from(file)
        this.search(Quiver.packDirectory!! / "assets" / asset.id, asset.path, asset.asset)
        searchedFile?.let { searchedFile ->
            EventSelectFolder.trigger(searchedFile.parentFile)
            EventSelectFile.trigger(searchedFile)
            EventSearchFile.trigger(searchedFile)
        }
        return null
    }

    override fun getSourceOffset(): Int = offset

    private fun search(start: File, path: String, asset: String) {
        start.listFiles()?.forEach {
            if (it.isDirectory) {
                search(it, path, asset)
            } else if (it.isFile && it.absolutePath.contains("$path/$asset")) {
                searchedFile = it
            }
        }
    }
}
