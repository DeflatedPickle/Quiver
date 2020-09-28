package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.quiver.backend.util.*
import com.deflatedpickle.quiver.frontend.window.Window
import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.FinishLine
import com.deflatedpickle.rawky.ui.constraints.StickEast
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXRadioGroup
import org.jdesktop.swingx.JXTextArea
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.text.PlainDocument

class NewDialog : TaskDialog(Window, "New") {
    val namespaceEntry = JXTextField("Namespace").apply {
        toolTipText = "The name of the folder with all the assets; i.e. your username"
        (document as PlainDocument).documentFilter = Filters.FILE
    }
    val nameEntry = JXTextField("Name").apply {
        toolTipText = "The name of the pack directory; i.e. the name of the pack"
        (document as PlainDocument).documentFilter = Filters.FILE
    }
    val locationEntry = JXTextField("Location").apply {
        toolTipText = "The location of the pack, defaults to .minecraft/resourcepacks"
        (document as PlainDocument).documentFilter = Filters.PATH
    }

    val packVersionComboBox = JComboBox<Int>((1..6).toList().toTypedArray()).apply {
        setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list, PackUtil.packVersionToGameVersion(value),
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

    val packTypeGroup = JXRadioGroup(PackType.values()).apply {
        toolTipText = """
            Empty Pack - Creates an empty pack
            Default Pack - Extracts and copies the default pack for the given version
        """.trimIndent()
        isOpaque = false

        // The buttons have a gray background by default
        for (packType in PackType.values()) {
            this.getChildButton(packType).apply {
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
            defaultVersionComboBox.isEnabled = selectedValue == PackType.DEFAULT_PACK
        }
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

            this.add(JXLabel("Namespace:"), StickEast)
            this.add(namespaceEntry, FillHorizontalFinishLine)

            this.add(JXLabel("Resource Pack Name:"), StickEast)
            this.add(nameEntry, FillHorizontalFinishLine)

            this.add(JXLabel("Pack Location:"), StickEast)
            this.add(locationEntry, FillHorizontalFinishLine)

            /* Metadata */
            this.add(JXTitledSeparator("Metadata"), FillHorizontalFinishLine)

            this.add(JXLabel("Pack Version:"), StickEast)
            this.add(packVersionComboBox, FillHorizontalFinishLine)

            this.add(JXLabel("Description:"), StickEast)
            this.add(descriptionEntry, FillHorizontalFinishLine)

            /* Pack Type */
            this.add(JXTitledSeparator("Pack Type"), FillHorizontalFinishLine)

            this.add(JXPanel().apply {
                isOpaque = false

                this.add(packTypeGroup, FillHorizontal)

                this.add(defaultVersionComboBox, FinishLine)
            }, FillHorizontalFinishLine)
        }).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
        }
    }
}