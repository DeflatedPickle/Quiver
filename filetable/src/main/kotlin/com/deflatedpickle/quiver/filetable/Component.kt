/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.component.PluginPanel
import java.awt.BorderLayout
import javax.swing.JScrollPane

object Component : PluginPanel() {
    init {
        this.layout = BorderLayout()

        this.add(JScrollPane(FileTable), BorderLayout.CENTER)
    }
}
