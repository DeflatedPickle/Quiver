package com.deflatedpickle.treeviewer

import org.jdesktop.swingx.JXTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class Component : JXTree() {
    val root = DefaultMutableTreeNode()
    val valueModel = DefaultTreeModel(root)

    init {
        this.model = valueModel
        this.isEditable = false
        this.isRootVisible = false
    }
}