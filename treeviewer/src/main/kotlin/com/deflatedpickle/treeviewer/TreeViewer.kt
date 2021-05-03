/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonArray
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import com.deflatedpickle.quiver.filepanel.api.Viewer
import java.io.File
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

object TreeViewer : Viewer<File> {
    private val component = Component()
    private val json = Jankson.builder().build()

    override fun refresh(with: File) {
        component.root.removeAllChildren()
        (component.model as DefaultTreeModel).reload()

        addJSONObject(this.json.load(with), component.root)

        component.expandAll()
    }

    private fun addJSONObject(obj: JsonObject, parent: DefaultMutableTreeNode) {
        for ((key, value) in obj.entries) {
            val keyNode = DefaultMutableTreeNode(key)

            when (value) {
                is JsonPrimitive -> {
                    val childNode = DefaultMutableTreeNode(value)
                    keyNode.add(childNode)
                }
                is JsonArray -> {
                    for (i in value) {
                        when (i) {
                            is JsonPrimitive -> {
                                val childNode = DefaultMutableTreeNode(i)
                                keyNode.add(childNode)
                            }
                            is JsonObject -> addJSONObject(i, keyNode)
                        }
                    }
                }
                is JsonObject -> addJSONObject(value, keyNode)
            }

            parent.add(keyNode)
        }
    }

    override fun getComponent(): JComponent = component
    override fun getScroller(): JScrollPane = JScrollPane(getComponent())
}
