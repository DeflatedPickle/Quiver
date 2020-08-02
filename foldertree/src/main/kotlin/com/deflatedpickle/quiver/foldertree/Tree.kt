package com.deflatedpickle.quiver.foldertree

import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.backend.util.DocumentUtil
import com.deflatedpickle.quiver.filetable.Table
import org.jdesktop.swingx.JXTree
import java.io.File
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

object Tree : JXTree(DefaultMutableTreeNode()) {
    init {
        this.showsRootHandles = true
        this.isRootVisible = false
        this.scrollsOnExpand = true
        this.expandsSelectedPaths = true

        this.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        this.addTreeSelectionListener {
            val folder = (it.path.lastPathComponent as DefaultMutableTreeNode).userObject as File
            Table.refresh(folder)
            EventSelectFolder.trigger(folder)
        }
    }

    fun refreshAll() {
        this.removeAll()

        val document = DocumentUtil.current
        val fakeRoot = DefaultMutableTreeNode(DocumentUtil.current)
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