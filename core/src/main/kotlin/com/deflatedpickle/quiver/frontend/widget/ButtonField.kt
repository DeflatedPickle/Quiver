/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FinishLine
import java.awt.GridBagLayout
import javax.swing.text.DocumentFilter
import javax.swing.text.PlainDocument
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField

class ButtonField(
    prompt: String,
    tooltip: String,
    filter: DocumentFilter,
    actionString: String,
    action: (ButtonField) -> Unit
) : JXPanel() {
    val field = JXTextField(prompt).apply {
        toolTipText = tooltip
        (document as PlainDocument).documentFilter = filter
    }
    val button = JXButton(actionString).apply {
        addActionListener { action(this@ButtonField) }
    }

    init {
        this.isOpaque = false
        this.layout = GridBagLayout()

        add(this.field, FillHorizontal)
        add(this.button, FinishLine)
    }
}
