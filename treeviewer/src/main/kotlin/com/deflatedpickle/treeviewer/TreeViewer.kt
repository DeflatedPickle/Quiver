/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.treeviewer

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonArray
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.filepanel.api.Viewer
import com.deflatedpickle.undulation.extensions.find
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.tag.ArrayTag
import net.querz.nbt.tag.CompoundTag
import net.querz.nbt.tag.ListTag
import net.querz.nbt.tag.Tag
import java.io.File
import java.lang.reflect.Array
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.JToolBar
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

object TreeViewer : Viewer<File> {
    private val component = Component()
    private val json = Jankson.builder().build()

    override fun refresh(with: File) {
        component.root.removeAllChildren()
        (component.model as DefaultTreeModel).reload()

        when (with.extension) {
            "json", "mcmeta" -> addJSONObject(this.json.load(with), component.root)
            "nbt", "dat" -> {
                val nbt = NBTUtil.read(with)
                addNBT(nbt.tag, component.root, nbt.name)
            }
            "lang" -> addLang(with)
        }

        component.expandAll()
    }

    override fun getToolBars() = Viewer.ToolBarPosition(
        north = JToolBar("Breadcrumbs").apply { add(component.breadcrumbs) }
    )

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

    private fun addNBT(tag: Tag<*>, parent: DefaultMutableTreeNode, name: String = "") {
        val keyNode = DefaultMutableTreeNode(
            (if (name != "") "$name " else "") +
                (if (name != "") "(" else "") +
                "${tag::class.simpleName}" +
                (if (name != "") ")" else "")
        )

        when (tag) {
            is CompoundTag -> {
                tag.forEach { k, v ->
                    addNBT(v, keyNode, k)
                }
            }
            is ListTag<*> -> {
                if (tag.size() == 0) {
                    keyNode.userObject = "${keyNode.userObject} [Empty]"
                } else {
                    for (i in tag) {
                        addNBT(i, keyNode)
                    }
                }
            }
            is ArrayTag<*> -> {
                if (tag.length() == 0) {
                    keyNode.userObject = "${keyNode.userObject} [Empty]"
                } else {
                    for (i in 0..tag.length()) {
                        val childNode = DefaultMutableTreeNode(Array.get(tag.value, i))
                        keyNode.add(childNode)
                    }
                }
            }
            else -> {
                // val childNode = DefaultMutableTreeNode(tag.valueToString())
                // keyNode.add(childNode)

                keyNode.userObject = "${tag.valueToString()} (${tag::class.simpleName})"
            }
        }

        parent.add(keyNode)
    }

    private fun addLang(file: File) {
        if (Quiver.format > 3) {
            addJSONObject(this.json.load(file), component.root)
        } else {
            for (i in file.readLines().sorted()) {
                addOldLang(i, component.root)
            }
            purgeDuplicate(component.root)
        }
    }

    private fun addOldLang(text: String, parent: DefaultMutableTreeNode) {
        var last = parent

        val split = text.split("=")

        val depth = mutableListOf<String>()
        for (i in split.first().split(".")) {
            val found = this.component.find(depth)

            val childNode = DefaultMutableTreeNode(i)
            depth.add(i)

            last = if (found == null) {
                last.add(childNode)
                purgeDuplicate(component.root)
                childNode
            } else {
                found.add(childNode)
                childNode
            }
        }

        val childNode = DefaultMutableTreeNode(split.last())
        last.add(childNode)
    }

    // I'm lazy, so instead of checking, we'll just add and remove the duplicates
    private fun purgeDuplicate(node: DefaultMutableTreeNode) {
        val list = mutableListOf<String>()

        for (i in node.children()) {
            val s = (i as DefaultMutableTreeNode).userObject as String
            if (s in list) {
                node.remove(i)
            } else {
                list.add(s)
            }
            purgeDuplicate(i)
        }
    }

    override fun getComponent(): JComponent = component
    override fun getScroller(): JScrollPane = JScrollPane(getComponent())
}
