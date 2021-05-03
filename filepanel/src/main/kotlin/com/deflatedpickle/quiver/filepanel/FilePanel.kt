/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.filepanel.widget.ReplaceButton
import com.deflatedpickle.quiver.frontend.widget.editButton
import com.deflatedpickle.quiver.frontend.widget.openButton
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.FillVerticalStickEast
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.constraints.StickWest
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JSeparator
import javax.swing.SwingConstants
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField

object FilePanel : PluginPanel() {
    private val nameLabel = JXLabel("Name")
    val nameField = JXTextField("Name").apply { isEnabled = false }
    private val dotLabel = JXLabel(".")
    val typeField = JXTextField("Type").apply { isEnabled = false }

    private val fileSizeLabel = JXLabel("File Size")
    val fileSize = JXLabel()

    private val openButton = openButton(
        false,
        { Desktop.getDesktop().open(Quiver.selectedFile) },
        { Desktop.getDesktop().open(Quiver.selectedFile?.parentFile) }
    )

    private val editButton = editButton(false) { Desktop.getDesktop().edit(Quiver.selectedFile) }

    private val replaceButton = ReplaceButton().apply {
        isEnabled = false
    }

    val fileActionPanel = JXPanel().apply {
        this.layout = GridBagLayout()

        this.add(editButton, StickWest)
        this.add(openButton, StickWest)
        this.add(replaceButton, StickWest)
        this.add(JSeparator(SwingConstants.VERTICAL), FillVerticalStickEast)
    }

    private val widgetArray = arrayOf<JComponent>(
        // nameField,
        // typeField,
        editButton,
        openButton,
        replaceButton
    )

    val widgetPanel = JXPanel().apply {
        border = BorderFactory.createTitledBorder("View")
        layout = BorderLayout()
    }

    init {
        this.layout = GridBagLayout()

        this.add(nameLabel, StickEast)
        this.add(nameField, FillHorizontal)
        this.add(dotLabel)
        this.add(typeField, FillHorizontalFinishLine)

        this.add(fileSizeLabel, StickEast)
        this.add(fileSize, FillHorizontalFinishLine)

        // It doesn't actually fill
        // Not sure why ¯\_(ツ)_/¯
        this.add(fileActionPanel, FillHorizontalFinishLine)

        this.add(widgetPanel, FillBothFinishLine)
    }

    fun state(enabled: Boolean = true) {
        for (i in widgetArray) {
            i.isEnabled = enabled
        }
    }
}
