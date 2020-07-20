package com.deflatedpickle.quiver.frontend

import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.FinishLine
import com.deflatedpickle.rawky.ui.constraints.StickEast
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXTextField
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

object FilePanel : JPanel(true) {
    private val nameLabel = JXLabel("Name:")
    val nameField = JXTextField("Name").apply { isEnabled = false }
    private val dotLabel = JXLabel(".")
    val typeField = JXTextField("Type").apply { isEnabled = false }

    private val imageSizeLabel = JXLabel("Image Size:")
    private val imageSize = JXLabel()

    private val openButton = JXButton("Open").apply { isEnabled = false }
    private val editButton = JXButton("Edit").apply { isEnabled = false }

    private val widgetArray = arrayOf<JComponent>(
        this.nameField, this.typeField,
        this.openButton, this.editButton
    )

    init {
        this.layout = GridBagLayout()

        this.add(this.nameLabel, StickEast)
        this.add(this.nameField, FillHorizontal)
        this.add(this.dotLabel)
        this.add(this.typeField, FillHorizontalFinishLine)

        this.add(this.imageSizeLabel, StickEast)
        this.add(this.imageSize, FillHorizontalFinishLine)

        this.add(this.openButton)
        this.add(this.editButton, FinishLine)
    }

    fun state(enabled: Boolean = true) {
        for (i in this.widgetArray) {
            i.isEnabled = enabled
        }
    }
}