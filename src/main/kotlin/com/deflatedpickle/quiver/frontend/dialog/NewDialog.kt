package com.deflatedpickle.quiver.frontend.dialog

import com.deflatedpickle.quiver.frontend.window.Window
import com.deflatedpickle.rawky.ui.constraints.FillHorizontalFinishLine
import com.deflatedpickle.rawky.ui.constraints.StickEast
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXTextField
import org.oxbow.swingbits.dialog.task.TaskDialog
import java.awt.GridBagLayout
import javax.swing.JPanel

class NewDialog : TaskDialog(Window, "New") {
    private val resourcePackNameLabel = JXLabel("Resource Pack Name:")
    private val namespaceLabel = JXLabel("Namespace:")

    val nameEntry = JXTextField("Name")
    val namespaceEntry = JXTextField("Namespace")

    init {
        setCommands(StandardCommand.OK, StandardCommand.CANCEL)

        this.fixedComponent = JPanel().apply {
            isOpaque = false
            layout = GridBagLayout()

            this.add(resourcePackNameLabel, StickEast)
            this.add(nameEntry, FillHorizontalFinishLine)

            this.add(namespaceLabel, StickEast)
            this.add(namespaceEntry, FillHorizontalFinishLine)
        }
    }
}