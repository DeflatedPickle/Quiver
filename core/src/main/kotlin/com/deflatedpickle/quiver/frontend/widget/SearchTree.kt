/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import java.awt.GridBagLayout
import javax.swing.JScrollPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXSearchField
import org.jdesktop.swingx.JXTree

class SearchTree : JXPanel() {
    private val root = DefaultMutableTreeNode("root")
    val model = DefaultTreeModel(this.root)

    val searchField = JXSearchField().apply {
        searchMode = JXSearchField.SearchMode.INSTANT
    }

    val tree = JXTree(this.model)

    init {
        this.layout = GridBagLayout()

        this.tree.isRootVisible = false

        this.add(this.searchField, FillHorizontalFinishLine)
        this.add(JScrollPane(this.tree), FillBothFinishLine)
    }
}
