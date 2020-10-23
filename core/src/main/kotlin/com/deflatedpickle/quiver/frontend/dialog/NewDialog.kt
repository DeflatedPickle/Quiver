/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.backend.util.DotMinecraft
import com.deflatedpickle.quiver.backend.util.ExtraResourceType
import com.deflatedpickle.quiver.backend.util.Filters
import com.deflatedpickle.quiver.backend.util.PackType
import com.deflatedpickle.quiver.backend.util.PackUtil
import com.deflatedpickle.quiver.backend.util.VersionUtil
import com.deflatedpickle.quiver.frontend.widget.ButtonField
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.FinishLine
import com.deflatedpickle.rawky.ui.constraints.StickEast
import com.jidesoft.swing.CheckBoxList
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.text.PlainDocument
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXRadioGroup
import org.jdesktop.swingx.JXTextArea
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog

class NewDialog : TaskDialog(PluginUtil.window, "Create New Pack") {
    val nameEntry = JXTextField("Name").apply {
        toolTipText = "The name of the pack directory; i.e. the name of the pack"
        (document as PlainDocument).documentFilter = Filters.FILE
    }
    val locationEntry = ButtonField(
        "Location",
        "The location of the pack",
        Filters.PATH,
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
        this.field.text = DotMinecraft.dotMinecraft.resolve("resourcepacks").absolutePath
    }

    // We'll cache a few game versions here so we don't keep generating them
    private val packToVersion = Array(6) {
        PackUtil.packVersionToGameVersion(it + 1)
    }

    val packVersionComboBox = JComboBox<Int>((1..6).toList().toTypedArray()).apply {
        setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list, packToVersion[value - 1],
                index, isSelected, cellHasFocus
            )
        }

        toolTipText = "The version this pack will be based off of, different versions have different quirks; i.e. lang names"
        selectedItem = this.itemCount
    }
    val descriptionEntry = JXTextArea("Description").apply {
        toolTipText = "The description of the pack, used in pack.mcmeta"
        border = BorderFactory.createEtchedBorder()
    }

    val defaultVersionComboBox = JComboBox(DotMinecraft.versions.listFiles()!!.filter {
        it.name.matches(VersionUtil.RELEASE) /*|| it.name.matches(VersionUtil.ALPHA) || it.name.matches(VersionUtil.BETA)*/
    }.toTypedArray()).apply {
        for (i in itemCount - 1 downTo 0) {
            if (getItemAt(i).name.matches(VersionUtil.RELEASE)) {
                selectedIndex = i
                break
            }
        }
    }

    val extraResourceTree = CheckBoxList(ExtraResourceType.values()).apply {
        toolTipText = "Extra resources that aren't included in the default pack"
        selectAll()
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
            }
        }

        for (i in PackType.values()) {
            getChildButton(i).text = i.name
                .toLowerCase()
                .split("_")
                .joinToString(" ") { it.capitalize() }
        }

        addActionListener {
            val isDefaultPack = selectedValue == PackType.DEFAULT_PACK

            defaultVersionComboBox.isEnabled = isDefaultPack
            extraResourceTree.isEnabled = isDefaultPack
        }
        // This triggers the action listener
        selectedValue = PackType.EMPTY_PACK
    }

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

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

            /* Metadata */
            this.add(JXTitledSeparator("Metadata"), FillHorizontalFinishLine)

            this.add(JXLabel("Version" + ":"), StickEast)
            this.add(packVersionComboBox, FillHorizontalFinishLine)

            this.add(JXLabel("Description" + ":"), StickEast)
            this.add(descriptionEntry, FillHorizontalFinishLine)

            /* Pack Type */
            this.add(JXTitledSeparator("Type"), FillHorizontalFinishLine)

            this.add(JXPanel().apply {
                isOpaque = false

                this.add(packTypeGroup, FillHorizontal)

                this.add(defaultVersionComboBox, FinishLine)
            }, FillHorizontalFinishLine)

            this.add(JXTitledSeparator("Extra Vanilla Data"), FillHorizontalFinishLine)

            this.add(extraResourceTree, FillBothFinishLine)
        }).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
        }
    }
}
