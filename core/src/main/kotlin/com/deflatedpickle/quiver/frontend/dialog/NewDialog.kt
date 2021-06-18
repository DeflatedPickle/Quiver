/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.builder.Builder
import com.deflatedpickle.marvin.builder.FileBuilder
import com.deflatedpickle.marvin.dsl.DSLFileNode
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.backend.extension.toPatternFilter
import com.deflatedpickle.quiver.backend.util.DotMinecraft
import com.deflatedpickle.quiver.backend.util.ExtraResourceType
import com.deflatedpickle.quiver.backend.util.Filters
import com.deflatedpickle.quiver.backend.util.PackType
import com.deflatedpickle.quiver.backend.util.PackUtil
import com.deflatedpickle.quiver.backend.util.VersionUtil
import com.deflatedpickle.quiver.frontend.widget.ButtonField
import com.deflatedpickle.quiver.frontend.widget.FoldingNotificationLabel
import com.deflatedpickle.quiver.frontend.widget.ThemedBalloonTip
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.extensions.expandAll
import com.jidesoft.swing.CheckBoxList
import com.jidesoft.swing.CheckBoxTree
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXCollapsiblePane
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXRadioGroup
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Dimension
import java.awt.GridBagLayout
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.BorderFactory
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import javax.swing.text.PlainDocument
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

class NewDialog : TaskDialog(PluginUtil.window, "Create New Pack") {
    private val logger = LogManager.getLogger()

    private fun validationCheck() = nameEntry.text != "" &&
            locationEntry.field.text != "" && resolutionEntry.text != ""

    private fun validateResolution(text: String) = text.isNotEmpty() && text.toInt() / 16 == 0

    val postTaskQueue = mutableListOf<() -> Unit>()

    val nameEntry: JXTextField = JXTextField("Name").apply {
        toolTipText = "The name of the pack directory; i.e. the name of the pack"
        (document as PlainDocument).documentFilter = Filters.FILE

        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
            nameNotEmptyTip.isVisible = this.text == ""
        })
        // We have to initially fire the validation as we don't have access to the OK button
        SwingUtilities.invokeLater {
            fireValidationFinished(this.text != "")
        }
    }
    private val nameNotEmptyTip = ThemedBalloonTip(nameEntry, "Name must not be empty", initiallyVisible = true)

    val locationEntry: ButtonField = ButtonField(
        "Location",
        "The location of the pack",
        OSUtil.getOS().toPatternFilter(),
        "Open"
    ) {
        val directoryChooser = JFileChooser(
            it.field.text
        ).apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            it.field.text = directoryChooser.selectedFile.absolutePath
        }
    }.apply {
        this.field.text = DotMinecraft.resourcePacks.absolutePath

        this.field.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
            locationNotEmptyTip.isVisible = this.field.text == ""

            val path = Paths.get(field.text)

            if (Files.exists(path)) {
                if (path != DotMinecraft.resourcePacks.toPath()) {
                    // This path isn't in the resource packs, add a button to make a symbolic link
                    locationHelpCollapsable.setFields(
                        "Create Symbolic Link?",
                        "This path isn't in your resource pack folder, would you like to create a link to it?",
                        JXButton("OK").apply {
                            addActionListener {
                                logger.info("Creating a symlink to \"$path\" in \"${DotMinecraft.resourcePacks}\"...")
                                postTaskQueue.add {
                                    Files.createSymbolicLink(
                                        DotMinecraft.resourcePacks
                                            .resolve(nameEntry.text)
                                            .toPath(),
                                        path.resolve(nameEntry.text)
                                    )
                                }

                                locationHelpCollapsable.emptyAndClose()
                            }
                        }
                    )

                    locationHelpCollapsable.isCollapsed = false
                } else {
                    // Everything's fine, close the collapsable
                    locationHelpCollapsable.emptyAndClose()
                }
            } else {
                // The given path doesn't exist, add a button to make it
                locationHelpCollapsable.setFields(
                    "Missing Directory",
                    "This directory doesn't exist, would you like to create it?",
                    JXButton("OK").apply {
                        addActionListener {
                            logger.info("Creating a chain of directories at \"$path\"...")
                            postTaskQueue.add { path.toFile().mkdirs() }
                            locationHelpCollapsable.emptyAndClose()
                        }
                    }
                )

                locationHelpCollapsable.isCollapsed = false
            }
        })
    }
    private val locationNotEmptyTip = ThemedBalloonTip(locationEntry.field, "Location must not be empty", false)
    private val locationHelpCollapsable = FoldingNotificationLabel()

    val resolutionEntry: JXTextField = JXTextField("Resolution").apply {
        text = "16"

        toolTipText = "The resolution of the pack"
        (document as PlainDocument).documentFilter = Filters.INTEGER

        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
            resolutionEmptyTip.isVisible = this.text.isEmpty()
            resolutionWrongTip.isVisible = validateResolution(this.text)
        })
        // We have to initially fire the validation as we don't have access to the OK button
        SwingUtilities.invokeLater {
            fireValidationFinished(validateResolution(this.text))
        }
    }
    private val resolutionEmptyTip = ThemedBalloonTip(resolutionEntry, "Resolution must not be empty", initiallyVisible = false)
    private val resolutionWrongTip = ThemedBalloonTip(resolutionEntry, "Resolution must be a multiplication of 16", initiallyVisible = false)
    val resolutionCollapsable = JXCollapsiblePane().apply {
        layout = GridBagLayout()
        isAnimated = true
    }

    // We'll cache a few game versions here so we don't keep generating them
    val packToVersion = Array(6) {
        PackUtil.packVersionToGameVersion(it + 1)
    }

    val packVersionComboBox = JComboBox((1..6).toList().toTypedArray()).apply {
        setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list, packToVersion[value - 1],
                index, isSelected, cellHasFocus
            )
        }

        toolTipText =
            "The version this pack will be based off of, different versions have different quirks; i.e. lang names"
        selectedItem = this.itemCount
    }
    val descriptionEntry = JXTextField("Description").apply {
        toolTipText = "The description of the pack, used in pack.mcmeta"
        // border = BorderFactory.createEtchedBorder()
    }

    val defaultVersionComboBox = JComboBox(
        // Returns the versions or an empty list if there are none
        (DotMinecraft.versions.listFiles() ?: listOf<File>().toTypedArray()).filter {
            it.name.matches(VersionUtil.RELEASE) /*|| it.name.matches(VersionUtil.ALPHA) || it.name.matches(VersionUtil.BETA)*/
        }.toTypedArray()
    ).apply {
        for (i in itemCount - 1 downTo 0) {
            if (getItemAt(i).name.matches(VersionUtil.RELEASE)) {
                selectedIndex = i
                break
            }
        }
    }

    val folderStructureRootNode = DefaultMutableTreeNode("root").apply {
        fun loopNodes(list: List<Builder.Node<File>>, lastNode: DefaultMutableTreeNode) {
            for (i in list) {
                val nextNode = DefaultMutableTreeNode(i.get().name)
                lastNode.add(nextNode)
            }
        }

        fun loopBuilders(list: List<FileBuilder>, lastNode: DefaultMutableTreeNode) {
            for (i in list) {
                val nextNode = DefaultMutableTreeNode(i.firstNode.get().name)
                loopNodes(i.nodeList.filterIsInstance<DSLFileNode>(), nextNode)
                lastNode.add(nextNode)
                loopBuilders(i.builder.childBuilderList, nextNode)
            }
        }
        val emptyPack = PackUtil.createEmptyPack("", false)
        // loopNodes(emptyPack.nodeList.filterIsInstance<DSLFileNode>(), this)
        loopBuilders(emptyPack.childBuilderList, this)
    }
    val folderStructure = CheckBoxTree(folderStructureRootNode).apply {
        toolTipText = "Folder structure of an empty pack"
        isRootVisible = false
        this.expandAll()

        checkBoxTreeSelectionModel.addSelectionPath(TreePath(folderStructureRootNode))
        isDigIn = true
    }
    val folderStructureCollapsable = JXCollapsiblePane().apply {
        layout = GridBagLayout()
    }

    val extraResourceTree = CheckBoxList(ExtraResourceType.values()).apply {
        toolTipText = "Extra resources that aren't included in the default pack"
        selectAll()
    }
    val extraResourceCollapsable = JXCollapsiblePane().apply {
        layout = GridBagLayout()
    }

    val packTypeGroup = JXRadioGroup(PackType.values()).apply {
        isOpaque = false

        // The buttons have a gray background by default
        for (packType in PackType.values()) {
            this.getChildButton(packType).apply {
                toolTipText = when (packType) {
                    PackType.EMPTY_PACK -> "Creates an empty pack structure"
                    PackType.DEFAULT_PACK -> "Extracts and copies the default pack for the given version"
                }
                isOpaque = false

                addActionListener {
                    versionPanel.removeAll()
                    versionPanel.add(when (packType) {
                        PackType.EMPTY_PACK -> packVersionComboBox
                        PackType.DEFAULT_PACK -> defaultVersionComboBox
                    }, FillHorizontalFinishLine)
                }
            }
        }

        for (i in PackType.values()) {
            getChildButton(i).text = i.name
                .toLowerCase()
                .split("_")
                .joinToString(" ") { it.capitalize() }
        }

        // Disables the "Default Pack" radio button if there are no versions
        if (defaultVersionComboBox.model.size <= 0) {
            getChildButton(1).isEnabled = false
        }

        addActionListener {
            val isDefaultPack = selectedValue == PackType.DEFAULT_PACK

            defaultVersionComboBox.isEnabled = isDefaultPack
            extraResourceTree.isEnabled = isDefaultPack

            resolutionCollapsable.isCollapsed = isDefaultPack

            folderStructureCollapsable.isCollapsed = isDefaultPack
            extraResourceCollapsable.isCollapsed = !isDefaultPack

            if (isDefaultPack) {
                extraResourceTree.selectAll()
            } else {
                extraResourceTree.selectNone()
            }
        }
        // This triggers the action listener
        selectedValue = PackType.EMPTY_PACK
    }

    val versionPanel = JXPanel().apply {
        isOpaque = false
        layout = GridBagLayout()

        add(packVersionComboBox, FillHorizontalFinishLine)
    }

    init {
        setCommands(
            StandardCommand.OK,
            StandardCommand.CANCEL
        )

        this.defaultVersionComboBox.setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list,
                if (packTypeGroup.selectedValue == PackType.EMPTY_PACK) " ".repeat(16)
                else value.name,
                index,
                isSelected,
                cellHasFocus
            )
        }

        this.fixedComponent = JScrollPane(JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            /* Pack */
            this.add(JXTitledSeparator("Pack"), FillHorizontalFinishLine)

            this.add(JXLabel("Name" + ":"), StickEast)
            this.add(nameEntry, FillHorizontalFinishLine)

            this.add(JXLabel("Location" + ":"), StickEast)
            this.add(locationEntry, FillHorizontalFinishLine)

            this.add(locationHelpCollapsable, FillBothFinishLine)

            this.add(resolutionCollapsable.apply {
                this.add(JXLabel("Resolution" + ":"), StickEast)
                this.add(resolutionEntry, FillHorizontalFinishLine)
            }, FillBothFinishLine)

            /* Metadata */
            this.add(JXTitledSeparator("Metadata"), FillHorizontalFinishLine)

            this.add(JXLabel("Version" + ":"), StickEast)

            this.add(versionPanel, FillHorizontalFinishLine)

            this.add(JXLabel("Description" + ":"), StickEast)
            this.add(descriptionEntry, FillHorizontalFinishLine)

            /* Pack Type */
            this.add(JXTitledSeparator("Type"), FillHorizontalFinishLine)

            this.add(JXPanel().apply {
                isOpaque = false

                this.add(packTypeGroup, FillHorizontal)
            }, FillHorizontalFinishLine)

            this.add(folderStructureCollapsable.apply {
                this.add(JXTitledSeparator("Included Files"), FillHorizontalFinishLine)

                this.add(JScrollPane(folderStructure), FillBothFinishLine)
            }, FillBothFinishLine)

            this.add(extraResourceCollapsable.apply {
                this.add(JXTitledSeparator("Extra Vanilla Data"), FillHorizontalFinishLine)

                this.add(JScrollPane(extraResourceTree), FillBothFinishLine)
            }, FillBothFinishLine)
        }).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
            preferredSize = Dimension(600, 500)
        }
    }
}
