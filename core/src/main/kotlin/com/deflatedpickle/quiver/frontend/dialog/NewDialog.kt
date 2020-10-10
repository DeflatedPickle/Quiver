package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.haruhi.util.LangUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.backend.util.*
import com.deflatedpickle.quiver.frontend.widget.ButtonField
import com.deflatedpickle.rawky.ui.constraints.FillHorizontal
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.FinishLine
import com.deflatedpickle.rawky.ui.constraints.StickEast
import org.jdesktop.swingx.*
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.io.File
import javax.swing.*
import javax.swing.text.PlainDocument

val lang = LangUtil.getLang("deflatedpickle@quiver#1.2.0")

class NewDialog : TaskDialog(PluginUtil.window, lang.trans("dialog.new.title")) {
    val namespaceEntry = JXTextField(lang.trans("dialog.new.namespace")).apply {
        toolTipText = lang.trans("dialog.new.namespace.tooltip")
        (document as PlainDocument).documentFilter = Filters.FILE
    }
    val nameEntry = JXTextField(lang.trans("dialog.new.name")).apply {
        toolTipText = lang.trans("dialog.new.name.tooltip")
        (document as PlainDocument).documentFilter = Filters.FILE
    }
    val locationEntry = ButtonField(
        lang.trans("dialog.new.location"),
        lang.trans("dialog.new.location.tooltip"),
        Filters.PATH,
        lang.trans("dialog.new.location.open")
    ) {
        val directoryChooser = JFileChooser().apply {
            currentDirectory = File(".")
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        val openResult = directoryChooser.showOpenDialog(PluginUtil.window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            it.field.text = directoryChooser.selectedFile.absolutePath
        }
    }

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

        toolTipText = lang.trans("dialog.new.version.tooltip")
        selectedItem = this.itemCount
    }
    val descriptionEntry = JXTextArea(lang.trans("dialog.new.description")).apply {
        toolTipText = lang.trans("dialog.new.description.tooltip")
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
        isOpaque = false

        // The buttons have a gray background by default
        for (packType in PackType.values()) {
            this.getChildButton(packType).apply {
                toolTipText = when(packType) {
                    PackType.EMPTY_PACK -> lang.trans("dialog.new.type.empty.tooltip")
                    PackType.DEFAULT_PACK -> lang.trans("dialog.new.type.default.tooltip")
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
            this.add(JXTitledSeparator(lang.trans("dialog.new.separator.pack")), FillHorizontalFinishLine)

            this.add(JXLabel(lang.trans("dialog.new.namespace") + ":"), StickEast)
            this.add(namespaceEntry, FillHorizontalFinishLine)

            this.add(JXLabel(lang.trans("dialog.new.name") + ":"), StickEast)
            this.add(nameEntry, FillHorizontalFinishLine)

            this.add(JXLabel(lang.trans("dialog.new.location") + ":"), StickEast)
            this.add(locationEntry, FillHorizontalFinishLine)

            /* Metadata */
            this.add(JXTitledSeparator(lang.trans("dialog.new.metadata")), FillHorizontalFinishLine)

            this.add(JXLabel(lang.trans("dialog.new.version") + ":"), StickEast)
            this.add(packVersionComboBox, FillHorizontalFinishLine)

            this.add(JXLabel(lang.trans("dialog.new.description") + ":"), StickEast)
            this.add(descriptionEntry, FillHorizontalFinishLine)

            /* Pack Type */
            this.add(JXTitledSeparator(lang.trans("dialog.new.type")), FillHorizontalFinishLine)

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