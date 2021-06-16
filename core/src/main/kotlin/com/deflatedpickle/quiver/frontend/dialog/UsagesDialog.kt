/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventSearchFile
import com.deflatedpickle.quiver.backend.event.EventSelectFile
import com.deflatedpickle.quiver.backend.event.EventSelectFolder
import com.deflatedpickle.quiver.backend.extension.toAsset
import com.deflatedpickle.quiver.backend.extension.toSyntaxEditingStyle
import com.deflatedpickle.quiver.backend.util.DotMinecraft
import com.deflatedpickle.quiver.frontend.widget.editButton
import com.deflatedpickle.quiver.frontend.widget.openButton
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.jidesoft.swing.DefaultOverlayable
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rtextarea.SearchContext
import org.fife.ui.rtextarea.SearchEngine
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXPanel
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Desktop
import java.awt.Dimension
import java.awt.GridBagLayout
import java.io.File
import javax.swing.BorderFactory
import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.JProgressBar
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JToolBar

// TODO: Add a context menu to select items in the file table
class UsagesDialog : TaskDialog(PluginUtil.window, "Find Usages") {
    private var lookFor: String = ""

    private val textArea = RSyntaxTextArea().apply {
        this.isEditable = false

        this.antiAliasingEnabled = true
        this.isWhitespaceVisible = true
        this.paintTabLines = true
        this.isCodeFoldingEnabled = true
    }

    private val listModel = DefaultListModel<File>()
    private val list = JList(this.listModel).apply {
        cellRenderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component = super.getListCellRendererComponent(
                list,
                (value as File).absolutePath.removePrefix("${DotMinecraft.resourcePacks.absolutePath}/"),
                index,
                isSelected,
                cellHasFocus
            )
        }

        addListSelectionListener {
            if (!this.valueIsAdjusting && this.model.size > 0) {
                textArea.syntaxEditingStyle = this.selectedValue.extension.toSyntaxEditingStyle()
                textArea.text = this.selectedValue.readText()

                val context = SearchContext().apply {
                    matchCase = false
                    markAll = true
                    wholeWord = true
                    searchForward = false
                    searchFor = lookFor
                }
                SearchEngine.find(textArea, context)
            }
        }
    }

    private val showButton = JXButton(MonoIcon.SHOW).apply {
        addActionListener {
            // println(list.selectedValue.parentFile)
            EventSelectFolder.trigger(list.selectedValue.parentFile)
            EventSelectFile.trigger(list.selectedValue)
            EventSearchFile.trigger(list.selectedValue)

            this@UsagesDialog.isVisible = false
        }
    }

    private val openButton = openButton(
        true,
        { Desktop.getDesktop().open(list.selectedValue) },
        { Desktop.getDesktop().open(list.selectedValue.parentFile) }
    )

    private val editButton = editButton(true) { Desktop.getDesktop().edit(list.selectedValue) }

    private val progressBar = JProgressBar().apply {
        isIndeterminate = true
    }

    init {
        setCommands(
            StandardCommand.OK
        )

        this.fixedComponent = JScrollPane(
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(
                    JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        DefaultOverlayable(
                            JScrollPane(list),
                            progressBar
                        ),
                        JXPanel(BorderLayout()).apply {
                            add(
                                JToolBar().apply {
                                    add(showButton)
                                    add(openButton)
                                    add(editButton)
                                },
                                BorderLayout.NORTH
                            )
                            add(RTextScrollPane(textArea), BorderLayout.CENTER)
                        }
                    ).apply {
                        isContinuousLayout = true
                        resizeWeight = 0.4
                    },
                    FillBothFinishLine
                )
            }
        ).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()

            preferredSize = Dimension(600, 400)
        }
    }

    fun refreshAll(lookFor: File) {
        this.lookFor = lookFor.nameWithoutExtension

        this.listModel.removeAllElements()
        this.refresh(Quiver.packDirectory!!, lookFor)
        progressBar.isVisible = false
    }

    fun refresh(file: File, lookFor: File) {
        file.listFiles()?.forEach {
            if (it.isDirectory) {
                refresh(it, lookFor)
            } else if (it.isFile) {
                val text = it.readText()

                if (text.contains(lookFor.nameWithoutExtension) ||
                    text.contains(lookFor.toAsset())
                ) {
                    // println(it)
                    this.listModel.addElement(it)
                }
            }
        }

        this.list.setSelectionInterval(0, 0)
    }
}
