package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.ui.constraints.*
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import java.awt.Desktop
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComponent

object Component : PluginPanel() {
    private val nameLabel = JXLabel("Name:")
    val nameField = JXTextField("Name").apply { isEnabled = false }
    private val dotLabel = JXLabel(".")
    val typeField = JXTextField("Type").apply { isEnabled = false }

    private val fileSizeLabel = JXLabel("File Size:")
    val fileSize = JXLabel()

    private val openButton = JXButton("Open").apply {
        isEnabled = false

        addActionListener {
            Desktop.getDesktop().open(FilePanel.selectedFile)
        }
    }

    private val editButton = JXButton("Edit").apply {
        isEnabled = false

        addActionListener {
            Desktop.getDesktop().edit(FilePanel.selectedFile)
        }
    }

    private val widgetArray = arrayOf<JComponent>(
        // nameField,
        // typeField,
        editButton,
        openButton
    )

    val widgetPanel = JXPanel().apply {
        border = BorderFactory.createTitledBorder("View")
        layout = GridBagLayout()
    }

    init {
        this.layout = GridBagLayout()

        this.add(nameLabel, StickEast)
        this.add(nameField, FillHorizontal)
        this.add(dotLabel)
        this.add(typeField, FillHorizontalFinishLine)

        this.add(fileSizeLabel, StickEast)
        this.add(fileSize, FillHorizontalFinishLine)

        this.add(editButton, StickWest)
        this.add(openButton, StickWestFinishLine)

        this.add(widgetPanel, FillBothFinishLine)
    }

    fun state(enabled: Boolean = true) {
        for (i in widgetArray) {
            i.isEnabled = enabled
        }
    }
}