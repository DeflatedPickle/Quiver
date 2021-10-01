@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.quiver.packcreator.dialog

import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.backend.extension.toPatternFilter
import com.deflatedpickle.quiver.backend.util.DotMinecraft
import com.deflatedpickle.quiver.backend.util.Filters
import com.deflatedpickle.quiver.backend.util.PackType
import com.deflatedpickle.quiver.frontend.widget.ButtonField
import com.deflatedpickle.quiver.packcreator.PackCreatorPlugin
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.constraints.FillBoth
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontal
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXRadioGroup
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.Color
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.GridBagLayout
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ProgressMonitor
import javax.swing.SwingUtilities
import javax.swing.text.PlainDocument

class NewDialog : TaskDialog(PluginUtil.window, "Create New Pack") {
    companion object {
        fun open() {
            val dialog = NewDialog()
            dialog.packKindGroup.getChildButton(0).apply {
                isSelected = true
                actionListeners[0].actionPerformed(
                    ActionEvent(
                        this,
                        ActionEvent.ACTION_PERFORMED,
                        null
                    )
                )
            }
            dialog.isVisible = true

            if (dialog.result == StandardCommand.OK) {
                val path = "${
                    if (dialog.locationEntry.field.text == "")
                        System.getProperty("user.dir")
                    else
                        dialog.locationEntry.field.text
                }/${dialog.nameEntry.text}"

                val stepProgress = ProgressMonitor(
                    PluginUtil.window,
                    dialog.packKindGroup.selectedValue.name,
                    "Starting...",
                    0,
                    Int.MAX_VALUE
                ).apply {
                    millisToPopup = 0
                    millisToDecideToPopup = 0
                }

                Quiver.packDirectory = File(path).apply {
                    mkdirs()
                    createNewFile()
                }

                EventNewDocument.trigger(Quiver.packDirectory!!)
                EventOpenPack.trigger(Quiver.packDirectory!!)

                dialog.packKindGroup.selectedValue.resolve(
                    dialog.nameEntry.text,
                    dialog.descriptionEntry.text,
                    Quiver.packDirectory!!,
                    stepProgress
                )
            }
        }
    }

    private fun validationCheck() = nameEntry.text != "" &&
            locationEntry.field.text != "" && packKindGroup.selectedValue.validate()

    val nameEntry: JXTextField = JXTextField("Name").apply {
        toolTipText = "The name of the pack"
        (document as PlainDocument).documentFilter = Filters.FILE

        this.document.addDocumentListener(DocumentAdapter {
            fireValidationFinished(validationCheck())
        })
        // We have to initially fire the validation as we don't have access to the OK button
        SwingUtilities.invokeLater {
            fireValidationFinished(this.text != "")
        }
    }

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
    }

    val descriptionEntry = JXTextField("Description").apply {
        toolTipText = "The description of the pack used in pack.mcmeta"
        // border = BorderFactory.createEtchedBorder()
    }

    val versionPanel = JXPanel().apply {
        isOpaque = false
        layout = GridBagLayout()
    }

    val packPanel = JPanel().apply {
        isOpaque = false
        layout = GridBagLayout()
    }
    val packKindGroup = JXRadioGroup(PackCreatorPlugin.packRegistry.getAll().values.toTypedArray()).apply {
        isOpaque = false

        for (i in PackCreatorPlugin.packRegistry.getAll().values) {
            getChildButton(i).apply {
                isOpaque = false
                text = i.name
                toolTipText = i.description

                addActionListener {
                    versionPanel.removeAll()
                    versionPanel.add(i.versions, FillHorizontalFinishLine)

                    packPanel.removeAll()
                    packPanel.add(i.panel, FillBothFinishLine)

                    this@NewDialog.fixedComponent.apply {
                        revalidate()
                        repaint()
                    }
                }
            }
        }
    }

    init {
        setCommands(
            StandardCommand.OK,
            StandardCommand.CANCEL
        )

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
            this.add(versionPanel, FillHorizontalFinishLine)

            this.add(JXLabel("Description" + ":"), StickEast)
            this.add(descriptionEntry, FillHorizontalFinishLine)

            /* Pack Type */
            this.add(JXTitledSeparator("Type"), FillHorizontalFinishLine)

            // This panel only exists to center the radio buttons
            this.add(JXPanel().apply {
                isOpaque = false

                this.add(packKindGroup, FillHorizontal)
            }, FillHorizontalFinishLine)

            this.add(packPanel, FillBothFinishLine)
        }).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
            preferredSize = Dimension(600, 500)
        }
    }
}