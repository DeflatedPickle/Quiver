/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.foldertree

import com.deflatedpickle.haruhi.component.PluginPanel
import java.awt.BorderLayout
import javax.swing.JScrollPane

object Component : PluginPanel() {
    init {
        this.layout = BorderLayout()

        this.add(JScrollPane(FolderTree), BorderLayout.CENTER)
    }
}
