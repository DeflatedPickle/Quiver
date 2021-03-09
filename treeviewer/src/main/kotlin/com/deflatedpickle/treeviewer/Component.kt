/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import org.jdesktop.swingx.JXTree

class Component : JXTree() {
    val root = DefaultMutableTreeNode()
    val valueModel = DefaultTreeModel(root)

    init {
        this.model = valueModel
        this.isEditable = false
        this.isRootVisible = false
    }
}
