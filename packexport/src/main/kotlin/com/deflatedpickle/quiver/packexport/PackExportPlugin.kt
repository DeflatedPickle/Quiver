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
import com.deflatedpickle.quiver.frontend.extension.add
import com.deflatedpickle.quiver.launcher.window.Toolbar
import com.deflatedpickle.quiver.packexport.api.BulkExportStep
import com.deflatedpickle.quiver.packexport.api.ExportStep
import com.deflatedpickle.quiver.packexport.api.PerFileExportStep
import java.io.File
import java.nio.file.Files
import javax.swing.JMenu
import net.lingala.zip4j.ZipFile
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialog
import org.oxbow.swingbits.dialog.task.TaskDialogs

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

    init {
        EventProgramFinishSetup.addListener {
            RegistryUtil.register("export", Registry<String, MutableList<ExportStep>>())

            val menuBar = RegistryUtil.get(MenuCategory.MENU.name)
            (menuBar?.get(MenuCategory.FILE.name) as JMenu).apply {
                add("Export Pack", NagatoIcon.CUT) { openExportPackGUI() }
                addSeparator()
            }

            Toolbar.add(NagatoIcon.CUT, "Export Pack") { openExportPackGUI() }
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
                    // FIXME: This line throws an error, thinking obvious subtypes of ExportStep are just Object
                    *(dialog.exportStepToggleList.checkBoxListSelectedValues)
                )
            }
        }
    }

    private fun exportPack(
        destination: String,
        vararg toggledSteps: Any
    ) {
        this.logger.info("Attempting to export the pack")

        if (Quiver.packDirectory == null) {
            this.logger.warn("You need to open a pack before exporting it")
            return
        } else if (toggledSteps.any { it !is ExportStep }) {
            this.logger.warn("Toggled steps must only contain instances of ExportStep")
            return
        }

        @Suppress("UNCHECKED_CAST")
        val registry = RegistryUtil.get("export") as Registry<String, ExportStep>

        // We need to copy everything to a temporary directory first
        // as they are processed then copied to the destination as a zip
        val tempPack = Files.createTempDirectory("quiver-${Quiver.packDirectory!!.name}")
        val tempPackFile = tempPack.toFile()
        this.logger.debug("Created a temporary file at: $tempPack")

        Quiver.packDirectory!!.copyRecursively(
            tempPackFile,
            true
        ) { file, ioException ->
            this.logger.warn("There was a problem copying $file to $tempPackFile, skipping it")
            this.logger.error(ioException)
            OnErrorAction.SKIP
        }

        val contents = tempPackFile.listFiles()

        contents?.let {
            // Alright, so you're probably reading this like;
            // "whaaaaa why would you loop every file for every step?"
            // And that's a good question, my compliments to you. Well done, here's a golden star sticker
            // I was doing that at first, but then I realised I wanted multiple kinds of steps, which requires me to check what kind of step it is
            // Now, that alone wouldn't require this, but as I want a step to run on every file, then a bulk step to run on the whole pack, we have to do this
            // An alternative could be a separate registries for single or bulk steps, but I'm not doing that
            for ((name, step) in registry.getAll()) {
                this.logger.trace("Starting the $step step")

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
                            step.processFile(file)
                            this.logger.debug("Finished the $name step for $file")
                        }
                    }
                    is BulkExportStep -> if (step in toggledSteps) {
                        this.logger.debug("Running the $name step for ${Quiver.packDirectory}")
                        step.processFile(Quiver.packDirectory!!)
                        this.logger.debug("Finished the $name step for ${Quiver.packDirectory}")
                    }
                }
            }
        }
    }
}