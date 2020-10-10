package com.deflatedpickle.quiver.filepanel

import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.rawky.ui.constraints.*
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import java.awt.BorderLayout
import java.awt.Desktop
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.border.TitledBorder

object Component : PluginPanel() {
    private val nameLabel = JXLabel()
    val nameField = JXTextField().apply { isEnabled = false }
    private val dotLabel = JXLabel(".")
    val typeField = JXTextField().apply { isEnabled = false }

    private val fileSizeLabel = JXLabel()
    val fileSize = JXLabel()

    private val openButton = JXButton().apply {
        isEnabled = false

        addActionListener {
            Desktop.getDesktop().open(FilePanel.selectedFile)
        }
    }

    private val editButton = JXButton().apply {
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
        border = BorderFactory.createTitledBorder("")
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

        this.add(editButton, StickWest)
        this.add(openButton, StickWestFinishLine)

        this.add(widgetPanel, FillBothFinishLine)

        EventProgramFinishSetup.addListener {
            val lang = LangUtil.getLang("deflatedpickle@file_panel#1.0.0")

            this.nameLabel.text = "${lang.trans("file.name")}:"
            this.nameField.prompt = lang.trans("file.name")
            this.typeField.prompt = lang.trans("file.type")
            this.fileSizeLabel.text = "${lang.trans("file.size")}:"
            this.openButton.text = lang.trans("file.open")
            this.editButton.text = lang.trans("file.edit")
            (this.widgetPanel.border as TitledBorder).title = lang.trans("file.view")
        }
    }

    fun state(enabled: Boolean = true) {
        for (i in widgetArray) {
            i.isEnabled = enabled
        }
    }
}