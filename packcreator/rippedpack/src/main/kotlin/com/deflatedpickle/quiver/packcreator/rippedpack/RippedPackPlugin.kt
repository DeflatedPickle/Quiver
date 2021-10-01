package com.deflatedpickle.quiver.packcreator.rippedpack

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.util.ConfigUtil
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.marvin.dsl.cabinet
import com.deflatedpickle.marvin.dsl.dir
import com.deflatedpickle.marvin.exceptions.UnsupportedOperatingSystemException
import com.deflatedpickle.marvin.extensions.Thread
import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.util.DotMinecraft
import com.deflatedpickle.quiver.backend.util.PackUtil
import com.deflatedpickle.quiver.backend.util.VersionUtil
import com.deflatedpickle.quiver.packcreator.PackCreatorPlugin
import com.deflatedpickle.quiver.packcreator.api.PackKind
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.extensions.expandAll
import com.deflatedpickle.undulation.extensions.findNode
import com.deflatedpickle.undulation.extensions.isSelected
import com.deflatedpickle.undulation.extensions.toDefaultMutableTreeNode
import com.deflatedpickle.undulation.extensions.updateUIRecursively
import com.jidesoft.swing.CheckBoxTree
import org.apache.logging.log4j.LogManager
import org.jdesktop.swingx.JXPanel
import java.awt.GridBagLayout
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import javax.swing.JComboBox
import javax.swing.JScrollPane
import javax.swing.ProgressMonitor
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        Provides a way to make vanilla-compatible packs
    """,
    type = PluginType.OTHER,
    dependencies = ["deflatedpickle@pack_creator#*"],
    settings = RippedPackSettings::class
)
object RippedPackPlugin : PackKind {
    private val logger = LogManager.getLogger()

    // This seems easier to think of than the old ExtraResourceType enum
    private val extraResourceRootNode = cabinet("", false) {
        dir("icons") {}
        dir("minecraft") {
            dir("icons") {}
            dir("lang") {}
            dir("sounds") {}
        }
        dir("realms") {
            dir("lang") {}
            dir("textures") {}
        }
    }.toDefaultMutableTreeNode()

    private val extraResourceTree = CheckBoxTree(extraResourceRootNode).apply {
        toolTipText = "Extra resources that aren't included in default packs"
        isRootVisible = false
        this.expandAll()

        checkBoxTreeSelectionModel.addSelectionPath(TreePath(extraResourceRootNode))
        isDigIn = true
    }

    override val name = "Ripped"
    override val description = "A pack constructed from the files of a Vanilla instance"
    override val panel = JXPanel().apply {
        isOpaque = false
        layout = GridBagLayout()

        add(JScrollPane(extraResourceTree), FillBothFinishLine)
    }
    override val versions: JComboBox<File> = JComboBox(
        // Returns the versions or an empty list if there are none
        (DotMinecraft.versions.listFiles() ?: listOf<File>().toTypedArray()).filter {
            it.name.matches(VersionUtil.RELEASE) /*|| it.name.matches(VersionUtil.ALPHA) || it.name.matches(VersionUtil.BETA)*/
        }.toTypedArray()
    ).apply {
        for (i in itemCount - 1 downTo 0) {
            if (getItemAt(i).name.matches(VersionUtil.RELEASE)) {
                selectedIndex = i
                break
            }
        }
    }

    init {
        PackCreatorPlugin.packRegistry.register("ripped", this)

        EventChangeTheme.addListener {
            panel.updateUIRecursively()
            versions.updateUI()
        }
    }

    override fun resolve(
        name: String,
        description: String,
        file: File,
        progressMonitor: ProgressMonitor
    ) {
        progressMonitor.maximum = 600

        println(extraResourceTree.isSelected("icons"))
        println(extraResourceTree.isSelected("minecraft/icons"))

        val system = when (OSUtil.getOS()) {
            OSUtil.OS.WINDOWS -> ".exe"
            OSUtil.OS.LINUX -> "-linux"
            OSUtil.OS.MAC -> "-macos"
            else -> throw UnsupportedOperatingSystemException(OSUtil.os)
        }

        ConfigUtil.getSettings<RippedPackSettings>("deflatedpickle@ripped_pack#*")?.let { settings ->
            val program = "${File(".").canonicalPath}/${settings.location}/${settings.executable}$system"

            val process = startResourceExtraction(file, program)
            val extraMinecraft = startMinecraftAssetExtraction(file, program)
            val extraRealms = startRealmsAssetExtraction(file, program)

            monitorTasks(progressMonitor, process, extraMinecraft, extraMinecraft)

            // file.parentFile.resolve((versions.selectedItem as File).name).renameTo(file)
        }

        PackUtil.writeMcMeta(
            PackUtil.gameVersionToPackVersion(Version.fromString((versions.selectedItem as File).name)),
            description
        )

        Quiver.resolution = PackUtil.findResolution()
    }

    private fun startResourceExtraction(file: File, program: String) =
        ProcessBuilder(
            program,
            "-d",
            "extract",
            "-n", file.name,
            "-v", (versions.selectedItem as File).name,
            "-o", file.parentFile.path,
            *mutableListOf<String>().apply {
                if (extraResourceTree.isSelected("icons")) add("-i")
                println(this)
            }.toTypedArray(),
            "version"
        )
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

    private fun startMinecraftAssetExtraction(file: File, program: String) = ProcessBuilder(
        program,
        "-d",
        "extract",
        "-n", file.name,
        "-v", (versions.selectedItem as File).name,
        "-o", file.parentFile.path,
        "asset",
        "minecraft",
        *mutableListOf<String>().apply {
            if (extraResourceTree.isSelected("minecraft/icons")) add("-i")
            if (extraResourceTree.isSelected("minecraft/lang")) add("-l")
            if (extraResourceTree.isSelected("minecraft/sounds")) add("-s")
            println(this)
        }.toTypedArray()
    )
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    private fun startRealmsAssetExtraction(file: File, program: String) = ProcessBuilder(
        program,
        "-d",
        "extract",
        "-n", file.name,
        "-v", (versions.selectedItem as File).name,
        "-o", file.parentFile.path,
        "asset",
        "realms",
        *mutableListOf<String>().apply {
            if (extraResourceTree.isSelected("realms/lang")) add("-l")
            if (extraResourceTree.isSelected("realms/textures")) add("-t")
            println(this)
        }.toTypedArray()
    )
        .redirectInput(ProcessBuilder.Redirect.PIPE)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    private fun monitorTasks(progressMonitor: ProgressMonitor, vararg task: Process) {
        var progress = 0

        Thread("mcpackutil") {
            for (i in task) {
                val outputStream = BufferedInputStream(
                    i.inputStream
                )

                try {
                    outputStream.bufferedReader().forEachLine {
                        if (progressMonitor.isCanceled) {
                            this.logger.debug("The mcpackutil task was cancelled")
                            i.destroyForcibly()
                            return@forEachLine
                        }

                        this.logger.trace(it)
                        progressMonitor.note = it
                        progressMonitor.setProgress(++progress)
                    }
                } catch (e: IOException) {
                }
            }
            progressMonitor.close()
        }.start()
    }

    override fun validate(): Boolean = true
}