/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import com.pump.swing.JBreadCrumb
import org.jdesktop.swingx.JXPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import org.jdesktop.swingx.JXTree
import java.awt.BorderLayout
import javax.swing.JScrollPane
import javax.swing.tree.TreeNode
import javax.swing.tree.TreePath

@Suppress("UNCHECKED_CAST")
class Component : JXTree() {
    val breadcrumbs = JBreadCrumb<Any>()

    val root = DefaultMutableTreeNode()
    private val valueModel = DefaultTreeModel(root)

    init {
        layout = BorderLayout()

        model = valueModel
        isEditable = false
        isRootVisible = false

        addTreeSelectionListener {
            breadcrumbs.setPath(*(it.path.path))
        }
    }
}
