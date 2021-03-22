/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.imageviewer

import blue.endless.jankson.Jankson
import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.api.Viewer
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import java.awt.event.ItemEvent
import java.io.File
import javax.swing.Box
import javax.swing.JToggleButton
import javax.swing.JToolBar
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel

@Suppress("unused")
@Plugin(
    value = "sprite_sheet_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for PNG files describing animated images
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
// TODO: Support image animation interpolation
object SpritesheetViewerPlugin {
    private val extensionSet = setOf(
        "png",
        "jpg", "jpeg"
    )

    // I know, this whole animation system is awful to look at
    // I'm sure it could be written a lot better, but I'm way too tired
    // If you have a better solution, feel free to submit a PR
    // But at least it works as-is, right?
    private var shouldAnimate = false
    private lateinit var animationThread: Thread

    private fun newAnimationThread() = Thread {
        while (shouldAnimate) {
            if (SpriteSheetViewer.index >= SpriteSheetViewer.maxIndex) {
                if (loopButton.isSelected) {
                    SpriteSheetViewer.index = 0
                } else {
                    shouldAnimate = false
                    playButton.isSelected = false
                }
            } else {
                SpriteSheetViewer.index++
            }
            Quiver.selectedFile?.let { SpriteSheetViewer.refresh(it) }

            try {
                Quiver.selectedFile?.let {
                    val meta = File("${it.parentFile.path}/${it.name}.mcmeta")
                    val json = if (meta.exists()) {
                        Jankson.builder().build().load(meta)
                    } else {
                        null
                    }

                    val frameTime = json?.getObject("animation")?.get(Int::class.java, "frametime")
                    Thread.sleep((1000L / 20) * (frameTime ?: 1))
                }
            } catch (e: InterruptedException) {
            }
        }
    }

    val mediaToolbar = JToolBar("Media")

    private val playButton: JToggleButton = JToggleButton(NagatoIcon.RUN).apply {
        addItemListener {
            when (it.stateChange) {
                ItemEvent.SELECTED -> {
                    if (!loopButton.isSelected && SpriteSheetViewer.index == SpriteSheetViewer.maxIndex) {
                        SpriteSheetViewer.index = 0
                    }

                    shouldAnimate = true
                    animationThread = newAnimationThread()
                    animationThread.start()
                }
                ItemEvent.DESELECTED -> {
                    shouldAnimate = false
                    animationThread.interrupt()
                }
            }
        }
    }

    private val loopButton = JToggleButton(NagatoIcon.RELOAD).apply {
        addItemListener {
            validateButtons()
        }
    }

    val toolbar = JToolBar("Navigate")

    private val nextButton = JXButton(NagatoIcon.ARROW_RIGHT).apply {
        addActionListener {
            if (loopButton.isSelected && SpriteSheetViewer.index >= SpriteSheetViewer.maxIndex) {
                SpriteSheetViewer.index = 0
            } else {
                SpriteSheetViewer.index++
            }
            Quiver.selectedFile?.let { SpriteSheetViewer.refresh(it) }
        }
    }
    val indexLabel = JXLabel()
    private val previousButton = JXButton(NagatoIcon.ARROW_LEFT).apply {
        addActionListener {
            if (loopButton.isSelected && SpriteSheetViewer.index <= 0) {
                SpriteSheetViewer.index = SpriteSheetViewer.maxIndex
            } else {
                SpriteSheetViewer.index--
            }
            Quiver.selectedFile?.let { SpriteSheetViewer.refresh(it) }
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
                        registry.register(i, mutableListOf(SpriteSheetViewer as Viewer<Any>))
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        registry.get(i)!!.add(SpriteSheetViewer as Viewer<Any>)
                    }
                }
            }
        }

        EventSelectFile.addListener {
            if (::animationThread.isInitialized) animationThread.interrupt()
            shouldAnimate = false

            if (it.extension !in extensionSet) return@addListener

            SpriteSheetViewer.index = 0
            SpriteSheetViewer.maxIndex = 0

            validateButtons()
            playButton.isSelected = false

            mediaToolbar.removeAll()
            toolbar.removeAll()

            if (File("${it.parentFile.path}/${it.name}.mcmeta").exists()) {
                mediaToolbar.add(Box.createGlue())
                mediaToolbar.add(playButton)
                mediaToolbar.add(loopButton)
                mediaToolbar.add(Box.createGlue())

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
        if (loopButton.isSelected) {
            this.nextButton.isEnabled = true
            this.previousButton.isEnabled = true
        } else {
            this.nextButton.isEnabled = SpriteSheetViewer.index < SpriteSheetViewer.maxIndex
            this.previousButton.isEnabled = SpriteSheetViewer.index > 0
        }
    }
}
