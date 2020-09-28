package com.deflatedpickle.quiver.frontend.widget.tagfield

import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import uk.co.timwise.wraplayout.WrapLayout
import java.awt.GridBagLayout
import java.util.*
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JPanel

class TagField : JPanel() {
    val tags = mutableListOf<String>()

    private val tagPanel = JPanel().apply {
        this.layout = WrapLayout()
        this.isOpaque = false
    }

    private val languageComboBox = JComboBox(
        Locale.getISOLanguages()
    ).apply {
        setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer()
                .getListCellRendererComponent(list, Locale(value).displayLanguage, index, isSelected, cellHasFocus)
        }
    }

    init {
        this.layout = GridBagLayout()
        this.isOpaque = false

        this.add(this.languageComboBox, FillHorizontalFinishLine)
        this.add(this.tagPanel, FillBothFinishLine)

        this.languageComboBox.addActionListener {
            if (!this.tags.contains(languageComboBox.selectedItem)) {
                val lang = this.languageComboBox.selectedItem as String

                tagPanel.add(TagComponent(lang))
                tagPanel.revalidate()
                tagPanel.repaint()

                this.tags.add(lang)
            }
        }
    }
}