package com.deflatedpickle.quiver.frontend.window

import com.deflatedpickle.marvin.builder.FileBuilder
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.frontend.FolderTree
import com.deflatedpickle.quiver.frontend.dialog.NewDialog
import org.jdesktop.swingx.JXButton
import java.io.File
import javax.swing.JToolBar

object Toolbar : JToolBar() {
    val newButton = JXButton("New").apply {
        addActionListener {
            val dialog = NewDialog()
            dialog.isVisible = true

            FileBuilder(dialog.nameEntry.text)
                .file("pack.mcmeta")
                .dir("assets")
                /*  */.dir("icons").build()
                /*  */.dir(dialog.namespaceEntry.text)
                /*      */.file("sounds.json")
                /*      */.dir("blockstates").build()
                /*      */.file("gpu_warnlist.json")
                /*      */.dir("font").build()
                /*      */.dir("icons").build()
                /*      */.dir("lang").build()
                /*      */.dir("models")
                /*          */.dir("block").build()
                /*          */.dir("item").build()
                /*      */.build()
                /*      */.dir("particles").build()
                /*      */.dir("sounds").build()
                /*      */.dir("shaders")
                /*          */.dir("post").build()
                /*          */.dir("program").build()
                /*      */.build()
                /*      */.dir("texts").build()
                /*      */.dir("textures")
                /*          */.dir("block").build()
                /*          */.dir("colormap").build()
                /*          */.dir("effect").build()
                /*          */.dir("entity").build()
                /*          */.dir("environment").build()
                /*          */.dir("font").build()
                /*          */.dir("gui")
                /*              */.dir("advancements")
                /*                  */.dir("backgrounds").build()
                /*              */.build()
                /*              */.dir("container")
                /*                  */.dir("creative_inventory")
                /*              */.build()
                /*              */.dir("presets").build()
                /*              */.dir("title")
                /*                  */.dir("background").build()
                /*              */.build()
                /*          */.build()
                /*          */.dir("item").build()
                /*          */.dir("map").build()
                /*          */.dir("misc").build()
                /*          */.dir("mob_effect").build()
                /*          */.dir("models")
                /*              */.dir("armor").build()
                /*          */.build()
                /*          */.dir("painting").build()
                /*          */.dir("particle")
                /*      */.build()
                /*      */
                /*  */.build()
                .build()
                .build()

            DocumentUtil.current = File(dialog.nameEntry.text).apply {
                mkdirs()
                createNewFile()
            }

            FolderTree.refreshAll()
        }
    }

    init {
        this.add(newButton)
    }

    fun createFile(name: String) = File(name).apply {
        mkdirs()
        createNewFile()
    }
}