/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.imageviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import java.io.File
import javax.swing.Box
import javax.swing.JToolBar
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel

@Suppress("unused")
@Plugin(
    value = "image_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for PNG files
    """,
    type = PluginType.OTHER
)
object ImageViewerPlugin {
    private val extensionSet = setOf(
        "png",
        "jpg", "jpeg"
    )

    val toolbar = JToolBar("Navigate")

    private val nextButton = JXButton(NagatoIcon.ARROW_RIGHT).apply {
        addActionListener {
            ImageViewer.index++
            Quiver.selectedFile?.let { ImageViewer.refresh(it) }
        }
    }
    val indexLabel = JXLabel()
    private val previousButton = JXButton(NagatoIcon.ARROW_LEFT).apply {
        addActionListener {
            ImageViewer.index--
            Quiver.selectedFile?.let { ImageViewer.refresh(it) }
        }
    }

    init {
        EventProgramFinishSetup.addListener {
            @Suppress("UNCHECKED_CAST")
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        @Suppress("UNCHECKED_CAST")
                        registry.register(i, mutableListOf(ImageViewer as Viewer<Any>))
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        registry.get(i)!!.add(ImageViewer as Viewer<Any>)
                    }
                }
            }
        }

        EventSelectFile.addListener {
            ImageViewer.index = 0
            ImageViewer.maxIndex = 0
            validateButtons()
            toolbar.removeAll()

            if (File("${it.parentFile.path}/${it.name}.mcmeta").exists()) {
                toolbar.add(Box.createGlue())
                toolbar.add(previousButton)
                toolbar.add(indexLabel)
                toolbar.add(nextButton)
                toolbar.add(Box.createGlue())
            }

            toolbar.repaint()
            toolbar.revalidate()
        }
    }

    internal fun validateButtons() {
        this.nextButton.isEnabled = ImageViewer.index < ImageViewer.maxIndex
        this.previousButton.isEnabled = ImageViewer.index > 0
    }
}
