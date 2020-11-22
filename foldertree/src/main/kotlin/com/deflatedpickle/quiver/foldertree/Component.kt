/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.foldertree

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import java.awt.BorderLayout
import java.awt.GridBagLayout
import javax.swing.JScrollPane

object Component : PluginPanel() {
    init {
        this.layout = BorderLayout()

        this.add(JScrollPane(FolderTree), BorderLayout.CENTER)
    }
}
