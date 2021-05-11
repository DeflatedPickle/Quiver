/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.packexport

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.constants.MenuCategory
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.nagato.NagatoIcon
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.event.EventNewDocument
import com.deflatedpickle.quiver.launcher.window.Toolbar
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import com.deflatedpickle.quiver.packexport.api.ExportStep
import com.deflatedpickle.quiver.packexport.api.PerFileExportStep
import com.deflatedpickle.quiver.packexport.event.EventExportFile
import com.deflatedpickle.quiver.packexport.event.EventFinishExportStep
import com.deflatedpickle.quiver.packexport.event.EventStartingExportStep
import com.deflatedpickle.undulation.extensions.add
import java.nio.file.Files
import java.util.Comparator
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.ProgressMonitor
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXButton
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs

@Suppress("unused")
@Plugin(
    value = "pack_export",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        An API to define modifications to files when the pack is processed into a ZIP
    """,
    type = PluginType.API
)
object PackExportPlugin {
    private val logger = LogManager.getLogger()

    private lateinit var toolbarButton: JXButton
    private lateinit var menuButton: JMenuItem

    init {
        EventProgramFinishSetup.addListener {
            RegistryUtil.register("export", Registry<String, MutableList<ExportStep>>())

            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                menuButton = add("Export Pack", NagatoIcon.CUT) { openExportPackGUI() }
                menuButton.isEnabled = false
                addSeparator()
            }

            toolbarButton = Toolbar.add(NagatoIcon.CUT, "Export Pack") { openExportPackGUI() }
            toolbarButton.isEnabled = false
        }

        EventNewDocument.addListener {
            if (it.exists() && this::toolbarButton.isInitialized && this::menuButton.isInitialized) {
                toolbarButton.isEnabled = true
                menuButton.isEnabled = true
            }
        }
    }

    private fun openExportPackGUI() {
        if (Quiver.packDirectory == null) {
            TaskDialogs.error(
                PluginUtil.window,
                "Can't Export Pack",
                "No resource pack has been opened to export"
            )
        } else {
            val dialog = ExportPackDialog()
            dialog.isVisible = true

            if (dialog.result == TaskDialog.StandardCommand.OK) {
                @Suppress("UNCHECKED_CAST")
                exportPack(
                    dialog.locationEntry.field.text,
                    *(dialog.exportStepToggleList.checkBoxListSelectedValues.filterIsInstance(ExportStep::class.java)
                        .toTypedArray())
                )
            }
        }
    }

    private fun exportPack(
        destination: String,
        vararg toggledSteps: ExportStep
    ) {
        this.logger.info("Attempting to export the pack")

        toggledSteps.sortWith(
            Comparator
                .comparing(ExportStep::getType)
                .thenComparing(ExportStep::getSlug)
        )
        this.logger.debug("Sorted the order of toggled export steps; $toggledSteps")

        if (Quiver.packDirectory == null) {
            this.logger.warn("You need to open a pack before exporting it")
            return
        }

        @Suppress("UNCHECKED_CAST")
        val registry = RegistryUtil.get("export") as Registry<String, ExportStep>

        // We need to copy everything to a temporary directory first
        // as they are processed then copied to the destination as a zip
        val tempPack = Files.createTempDirectory("quiver-${Quiver.packDirectory!!.name}")
        val tempPackFile = tempPack.toFile()
        this.logger.debug("Created a temporary file at: $tempPack")

        var progress = 0
        val progressMonitor = ProgressMonitor(
            PluginUtil.window,
            "Executing export steps",
            "Copying the open pack to a temporary directory",
            progress,
            1 + registry.getAll().size
        )

        Quiver.packDirectory!!.copyRecursively(
            tempPackFile,
            true
        ) { file, ioException ->
            this.logger.warn("There was a problem copying $file to $tempPackFile, skipping it")
            this.logger.error(ioException)
            OnErrorAction.SKIP
        }.also {
            if (it) {
                progressMonitor.setProgress(++progress)
            } else {
                TaskDialogs.error(
                    PluginUtil.window,
                    "Copying Canceled",
                    "The copying of the pack to a temporary copy was cancelled"
                )
                progressMonitor.close()
                return
            }
        }

        val contents = tempPackFile.listFiles()

        contents?.let {
            // Alright, so you're probably reading this like;
            // "whaaaaa why would you loop every file for every step?"
            // And that's a good question, my compliments to you. Well done, here's a golden star sticker
            // I was doing that at first, but then I realised I wanted multiple kinds of steps, which requires me to check what kind of step it is
            // Now, that alone wouldn't require this, but as I want a step to run on every file, then a bulk step to run on the whole pack, we have to do this
            // An alternative could be a separate registries for single or bulk steps, but I'm not doing that
            for (step in toggledSteps) {
                val stepProgress = ProgressMonitor(
                    PluginUtil.window,
                    step.getSlug().name,
                    "Starting...",
                    0,
                    Int.MAX_VALUE
                ).apply {
                    millisToPopup = 0
                    millisToDecideToPopup = 0
                }

                EventStartingExportStep.trigger(step)
                val name = step.getSlug().name

                when (step) {
                    is PerFileExportStep -> for (file in it) {
                        if (step in toggledSteps && (
                                    // Only run for the given extension
                                    // unless the step uses a wildcard
                                    file.extension in step.affectedExtensions ||
                                            step.affectedExtensions.contains("*")
                                    )
                        ) {
                            this.logger.debug("Running the $name step for $file")
                            progressMonitor.note = "Running the per-file $name step"
                            step.processFile(file, stepProgress)
                            EventExportFile.trigger(file)
                            progressMonitor.setProgress(++progress)
                            this.logger.debug("Finished the $name step for $file")
                        }
                    }
                    is BulkExportStep -> if (
                        step in toggledSteps
                    ) {
                        this.logger.debug("Running the $name step for ${Quiver.packDirectory}")
                        progressMonitor.note = "Running the bulk $name step"
                        step.processFile(Quiver.packDirectory!!, stepProgress)
                        progressMonitor.setProgress(++progress)
                        this.logger.debug("Finished the $name step for ${Quiver.packDirectory}")
                    }
                }

                EventFinishExportStep.trigger(step)
            }
        }

        progressMonitor.setProgress(++progress)
    }
}
