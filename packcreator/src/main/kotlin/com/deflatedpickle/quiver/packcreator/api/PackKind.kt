package com.deflatedpickle.quiver.packcreator.api

import java.io.File
import javax.swing.JComboBox
import javax.swing.JPanel
import javax.swing.ProgressMonitor

interface PackKind {
    /**
     * The name of this kind of pack
     */
    val name: String

    /**
     * A description for this kind of pack
     */
    val description: String

    /**
     * A panel to provide extra components related to this kind of pack
     */
    val panel: JPanel

    /**
     * A [JComboBox] to choose through versions of this kind of pack
     */
    val versions: JComboBox<*>

    /**
     * Create this kind of pack
     */
    fun resolve(
        name: String,
        description: String,
        file: File,
        progressMonitor: ProgressMonitor
    )

    /**
     * Add checks based on any components needing data
     *
     * These checks add up to if the dialog's OK button is enabled or disabled
     */
    fun validate(): Boolean
}