/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.frontend.extension.add
import javax.swing.JPopupMenu
import org.jdesktop.swingx.JXButton

fun openButton(enabled: Boolean, open: () -> Unit, openFolder: () -> Unit) = JSplitButton("  ", NagatoIcon.FOLDER_OPEN_FILE).apply {
    popupMenu = JPopupMenu("Open Alternatives").apply {
        this.add("Open Folder", NagatoIcon.FOLDER_OPEN) { openFolder() }
    }
    toolTipText = "Open File"
    isEnabled = enabled

    addButtonClickedActionListener {
        open()
    }
}

fun editButton(enabled: Boolean, action: () -> Unit) = JXButton(NagatoIcon.PENCIL).apply {
    toolTipText = "Edit"
    isEnabled = enabled

    addActionListener {
        action()
    }
}
