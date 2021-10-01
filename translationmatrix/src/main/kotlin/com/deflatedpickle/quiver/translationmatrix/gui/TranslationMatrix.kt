/* Copyright (c) 2021 DeflatedPickle under the MIT license */

@file:Suppress("DEPRECATION")

package com.deflatedpickle.quiver.translationmatrix.gui

import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.api.lang.LangKey
import com.deflatedpickle.quiver.backend.api.lang.LangReader
import com.deflatedpickle.quiver.translationmatrix.config.TranslationMatrixSettings
import com.deflatedpickle.undulation.predicate.highlight.AlternatePredicate
import com.deflatedpickle.undulation.predicate.highlight.EmptyPredicate
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXTable
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Component
import java.awt.Dimension
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SortOrder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

@OptIn(DelicateCoroutinesApi::class)
class TranslationMatrix : TaskDialog(
    PluginUtil.window,
    "Translation Matrix"
) {
    private val logger = LogManager.getLogger()

    companion object {
        fun open(
            onOK: () -> Unit = {},
            onCancel: () -> Unit = {},
        ): TranslationMatrix {
            val dialog = TranslationMatrix()
            dialog.isVisible = true

            when (dialog.result) {
                StandardCommand.OK -> onOK()
                StandardCommand.CANCEL -> onCancel()
            }

            return dialog
        }

        fun openAt(
            string: String,
            onOK: () -> Unit = {},
            onCancel: () -> Unit = {},
        ) {
            val dialog = open(onOK, onCancel)
            dialog.search(string)
        }
    }

    private val allKeys = mutableListOf<String>()

    private val dict = mutableMapOf<String, MutableList<LangKey>>()

    private val model = object : DefaultTableModel(arrayOf("Key"), 0) {
        override fun isCellEditable(row: Int, column: Int): Boolean = column != 0

        override fun getColumnClass(columnIndex: Int): Class<*> =
            when (columnIndex) {
                0 -> String::class.java
                else -> LangKey::class.java
            }
    }
    private val jxtable = JXTable(model).apply {
        autoResizeMode = JTable.AUTO_RESIZE_OFF
    }

    init {
        setCommands(
            StandardCommand.OK
        )

        this.fixedComponent = JScrollPane(jxtable).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
            preferredSize = Dimension(600, 400)
        }

        GlobalScope.launch {
            refreshAll()
        }

        addHighlighters()
        addLangKeyRenderer()
    }

    private fun addHighlighters() {
        jxtable.setHighlighters()
        AlternatePredicate(jxtable).apply()
        EmptyPredicate(jxtable).apply()
    }

    private fun addLangKeyRenderer() {
        jxtable.setDefaultRenderer(
            LangKey::class.java,
            object : DefaultTableCellRenderer() {
                override fun getTableCellRendererComponent(
                    table: JTable,
                    value: Any?,
                    isSelected: Boolean,
                    hasFocus: Boolean,
                    row: Int,
                    column: Int
                ): Component = super.getTableCellRendererComponent(
                    table, (value as LangKey?)?.value,
                    isSelected, hasFocus,
                    row, column
                )
            }
        )
    }

    private fun refreshAll() {
        jxtable.removeAll()
        model.rowCount = 0

        ConfigUtil.getSettings<TranslationMatrixSettings>("$[dev]@$[name]#>=1.0.0")?.langs?.let { langs ->
            refreshKeys(langs)
            // println(keys)
            refreshData(langs)
        }

        // println(dict)
        refreshModel()

        jxtable.columnModel.let {
            for ((i, o) in it.columns.toList().withIndex()) {
                o.minWidth = when (i) {
                    0 -> 360
                    else -> 200
                }
            }
        }

        if (jxtable.rowCount > 0) {
            jxtable.setRowSelectionInterval(0, 0)
        }

        if (jxtable.columns.size > 0) {
            jxtable.setSortOrder(0, SortOrder.ASCENDING)
        }
    }

    private fun refreshKeys(langs: List<String>) {
        allKeys.clear()

        LangReader.read(langs) { k, _ ->
            allKeys.add(k)
        }
    }

    private fun refreshData(langs: List<String>) {
        LangReader.read(langs) { k, v ->
            dict.getOrPut(k, ::mutableListOf) += LangKey(v, -1, k, "")
        }
    }

    private fun refreshModel() {
        dict.forEach { (_, v) ->
            // println(v)

            for (i in v) {
                if (model.findColumn(i.lang) == -1) {
                    model.addColumn(i.lang)
                }
            }

            model.addRow(arrayOf(v.first().key, *v.toTypedArray()))
        }
    }

    fun search(string: String) {
    }
}
