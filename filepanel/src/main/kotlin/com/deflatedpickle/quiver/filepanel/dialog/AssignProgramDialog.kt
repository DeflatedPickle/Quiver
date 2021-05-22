/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.dialog

import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.filepanel.api.Program
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.extensions.toPatternFilter
import com.deflatedpickle.undulation.util.Filters
import com.deflatedpickle.undulation.widget.ButtonField
import com.deflatedpickle.undulation.widget.taginput.TagInput
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import java.awt.Window
import java.io.File
import javax.swing.BorderFactory
import javax.swing.JFileChooser
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.SwingUtilities
import javax.swing.text.PlainDocument

class AssignProgramDialog(
    window: Window,
) : TaskDialog(window, "Assign Program") {
    companion object {
        fun open(window: Window): Program? {
            val dialog = AssignProgramDialog(window)
            dialog.isVisible = true

            if (dialog.result == StandardCommand.OK) {
                val program = Program(
                    dialog.nameEntry.text,
                    dialog.locationEntry.field.text.substringBeforeLast("/"),
                    dialog.locationEntry.field.text.substringAfterLast("/"),
                    dialog.argsEntry.text,
                    dialog.extensionsEntry.tags
                )

                return program
            }

            return null
        }
    }

    private fun validationCheck() =
        nameEntry.text != "" &&
            locationEntry.field.text.let {
                val dir = File(it.substringBeforeLast("/"))
                val program = it.substringAfterLast("/")
                val file = dir.resolve(program)

                it != "" && dir.exists() && dir.isDirectory &&
                    program != "" && file.exists() && file.isFile &&
                    extensionsEntry.tags.isNotEmpty()
            }

    val nameEntry: JXTextField = JXTextField("Name").apply {
        toolTipText = "The name to assign to this program"
        (document as PlainDocument).documentFilter = Filters.FILE

        this.document.addDocumentListener(
            DocumentAdapter {
                fireValidationFinished(validationCheck())
            }
        )
        SwingUtilities.invokeLater {
            fireValidationFinished(validationCheck())
        }
    }

    val locationEntry: ButtonField = ButtonField(
        "Location",
        "The location of the program",
        OSUtil.getOS().toPatternFilter(),
        "Open"
    ) {
        val directoryChooser = JFileChooser(
            it.field.text
        ).apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            isAcceptAllFileFilterUsed = false
        }
        val openResult = directoryChooser.showOpenDialog(window)

        if (openResult == JFileChooser.APPROVE_OPTION) {
            it.field.text = directoryChooser.selectedFile.absolutePath
        }
    }.apply {
        this.field.document.addDocumentListener(
            DocumentAdapter {
                fireValidationFinished(validationCheck())
            }
        )
    }

    val argsEntry: JXTextField = JXTextField("Args").apply {
        toolTipText = "The arguments the command will receive"
    }

    val extensionsEntry: TagInput = TagInput().apply {
        addChangeListener {
            fireValidationFinished(validationCheck())
        }
    }

    init {
        setCommands(
            StandardCommand.OK,
            StandardCommand.CANCEL
        )

        fixedComponent = JScrollPane(
            JPanel().apply {
                isOpaque = false
                layout = GridBagLayout()

                add(JXLabel("Name:"), StickEast)
                add(nameEntry, FillHorizontalFinishLine)

                add(JXLabel("Location:"), StickEast)
                add(locationEntry, FillHorizontalFinishLine)

                add(JSeparator(), FillHorizontalFinishLine)

                add(JXLabel("Args:"), StickEast)
                add(argsEntry, FillHorizontalFinishLine)

                add(JSeparator(), FillHorizontalFinishLine)

                add(JXLabel("Extensions:"), StickEast)
                add(extensionsEntry, FillHorizontalFinishLine)
            }
        ).apply {
            isOpaque = false
            viewport.isOpaque = false

            border = BorderFactory.createEmptyBorder()
        }
    }
}
