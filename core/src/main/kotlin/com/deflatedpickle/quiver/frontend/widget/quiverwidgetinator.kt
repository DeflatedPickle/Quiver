/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.alexandriasoftware.swing.JSplitButton
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.undulation.extensions.add
import org.jdesktop.swingx.JXButton
import javax.swing.JPopupMenu

fun openButton(enabled: Boolean, open: () -> Unit, openFolder: () -> Unit) = JSplitButton("  ", MonoIcon.FOLDER_OPEN_FILE).apply {
    popupMenu = JPopupMenu("Open Alternatives").apply {
        this.add("Open Folder", MonoIcon.FOLDER_OPEN) { openFolder() }
    }
    toolTipText = "Open File"
    isEnabled = enabled

    addButtonClickedActionListener {
        open()
    }
}

fun editButton(enabled: Boolean, action: () -> Unit) = JXButton(MonoIcon.PENCIL).apply {
    toolTipText = "Edit"
    isEnabled = enabled

    addActionListener {
        action()
    }
}
