/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.foldertree

import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.frontend.menu.FilePopupMenu
import java.awt.Component
import java.io.File
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel
import org.jdesktop.swingx.JXTree

object FolderTree : JXTree(DefaultMutableTreeNode()) {
    init {
        this.showsRootHandles = true
        this.isRootVisible = false
        this.scrollsOnExpand = true
        this.expandsSelectedPaths = true

        this.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        this.componentPopupMenu = FilePopupMenu {
            (FolderTree.selectionPath?.lastPathComponent as DefaultMutableTreeNode?)
                ?.userObject as File?
        }

        this.addSelectionListener()
        this.setCellRender()
    }

    private fun addSelectionListener() {
        this.addTreeSelectionListener {
            val folder = (it.path.lastPathComponent as DefaultMutableTreeNode).userObject as File
            EventSelectFolder.trigger(folder)
        }
    }

    private fun setCellRender() {
        this.setCellRenderer(object : DefaultTreeCellRenderer() {
            override fun getTreeCellRendererComponent(
                tree: JTree?,
                value: Any?,
                selected: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ): Component = super.getTreeCellRendererComponent(
                tree,
                ((value as DefaultMutableTreeNode).userObject as File?)?.name?.split("\\")?.last(),
                selected, expanded, leaf, row, hasFocus
            )
        })
    }

    fun refreshAll() {
        this.removeAll()

        val document = Quiver.packDirectory
        val fakeRoot = DefaultMutableTreeNode(Quiver.packDirectory)
        (this.model.root as DefaultMutableTreeNode).add(fakeRoot)
        refresh(document!!, fakeRoot)

        this.expandAll()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun refresh(file: File, node: DefaultMutableTreeNode) {
        file.listFiles()?.filter { it.isDirectory }?.forEach {
            val newNode = DefaultMutableTreeNode(it)
            (this.model as DefaultTreeModel).insertNodeInto(
                newNode,
                node,
                0
            )

            refresh(it, newNode)
        }

        this.setSelectionRow(0)
    }
}
