package com.deflatedpickle.quiver.frontend

import com.deflatedpickle.quiver.backend.util.DocumentUtil
import org.jdesktop.swingx.JXTree
import java.io.File
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeSelectionModel

object FolderTree : JXTree(DefaultMutableTreeNode()) {
    init {
        this.showsRootHandles = true
        this.isRootVisible = false
        this.scrollsOnExpand = true
        this.expandsSelectedPaths = true

        this.selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION

        SwingUtilities.invokeLater {
            this.removeAll()

            val document = DocumentUtil.current
            val fakeRoot = DefaultMutableTreeNode(DocumentUtil.current)
            (this.model.root as DefaultMutableTreeNode).add(fakeRoot)
            refresh(document!!, fakeRoot)

            this.expandAll()
        }

        this.addTreeSelectionListener {
            FileTable.refresh((it.path.lastPathComponent as DefaultMutableTreeNode).userObject as File)
        }
    }

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