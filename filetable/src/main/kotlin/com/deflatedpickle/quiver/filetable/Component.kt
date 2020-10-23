/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable

import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import java.awt.GridBagLayout
import javax.swing.JScrollPane

object Component : PluginPanel() {
    init {
        this.layout = GridBagLayout()

        this.add(JScrollPane(FileTable), FillBothFinishLine)
    }
}
