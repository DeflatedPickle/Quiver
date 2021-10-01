package com.deflatedpickle.quiver.packcreator.emptypack

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.marvin.builder.Builder
import com.deflatedpickle.marvin.builder.FileBuilder
import com.deflatedpickle.marvin.dsl.DSLFileNode
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.util.Filters
import com.deflatedpickle.quiver.backend.util.PackFormat
import com.deflatedpickle.quiver.backend.util.PackUtil
import com.deflatedpickle.undulation.extensions.updateUIRecursively
import com.deflatedpickle.quiver.packcreator.PackCreatorPlugin
import com.deflatedpickle.quiver.packcreator.api.PackKind
import com.deflatedpickle.sniffle.swingsettings.event.EventChangeTheme
import com.deflatedpickle.undulation.constraints.FillBothFinishLine
import com.deflatedpickle.undulation.constraints.FillHorizontalFinishLine
import com.deflatedpickle.undulation.constraints.StickEast
import com.deflatedpickle.undulation.extensions.expandAll
import com.deflatedpickle.undulation.extensions.isSelected
import com.deflatedpickle.undulation.extensions.toDefaultMutableTreeNode
import com.jidesoft.swing.CheckBoxTree
import org.jdesktop.swingx.JXLabel
import org.jdesktop.swingx.JXPanel
import org.jdesktop.swingx.JXTextField
import org.jdesktop.swingx.JXTitledSeparator
import java.awt.GridBagLayout
import java.io.File
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JScrollPane
import javax.swing.ProgressMonitor
import javax.swing.text.PlainDocument
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
    dependencies = ["deflatedpickle@pack_creator#*"]
)
object EmptyPackPlugin : PackKind {
    // We'll cache a few game versions here so that we don't keep generating them
    private val packToVersion = Array(6) {
        PackUtil.packVersionToGameVersion(it + 1)
    }

    private val resolutionEntry: JXTextField = JXTextField("Resolution").apply {
        text = "16"

        toolTipText = "The resolution of the pack"
        (document as PlainDocument).documentFilter = Filters.INTEGER
    }

    private val folderStructureRootNode = PackUtil.createEmptyPack("", false).toDefaultMutableTreeNode()
    private val folderStructure = CheckBoxTree(folderStructureRootNode).apply {
        toolTipText = "Folder structure of an empty pack"
        isRootVisible = false
        this.expandAll()

        checkBoxTreeSelectionModel.addSelectionPath(TreePath(folderStructureRootNode))
        isDigIn = true
    }

    override val name = "Empty"
    override val description = "An empty pack using the folder structure of the selected version"
    override val panel = JXPanel().apply {
        isOpaque = false
        layout = GridBagLayout()

        add(JXLabel("Resolution" + ":"), StickEast)
        add(resolutionEntry, FillHorizontalFinishLine)

        add(JXTitledSeparator("Included Files"), FillHorizontalFinishLine)
        add(JScrollPane(folderStructure), FillBothFinishLine)
    }
    override val versions: JComboBox<*> = JComboBox((1..6).toList().toTypedArray()).apply {
        setRenderer { list, value, index, isSelected, cellHasFocus ->
            DefaultListCellRenderer().getListCellRendererComponent(
                list, packToVersion[value - 1],
                index, isSelected, cellHasFocus
            )
        }

        toolTipText = "The version this pack will be based off of"
        selectedItem = this.itemCount
    }

    init {
        PackCreatorPlugin.packRegistry.register("empty", this)

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
        println("-------${folderStructure.isSelected("assets/minecraft")}")

        PackUtil.createEmptyPack(
            file.path, true,
            PackUtil.PackStructure(
                packMcMeta = true,
                assets = if (folderStructure.isSelected("assets")) PackUtil.PackStructure.Assets(
                    icons = folderStructure.isSelected("assets/icons"),
                    minecraft = if (folderStructure.isSelected("assets/minecraft")) PackUtil.PackStructure.Minecraft(
                        soundsJson = folderStructure.isSelected("assets/minecraft/sounds.json"),
                        blockstates = folderStructure.isSelected("assets/minecraft/blockstates"),
                        gpuWarnlistJson = folderStructure.isSelected("assets/minecraft/gpu_warnlist.json"),
                        font = folderStructure.isSelected("assets/minecraft/font"),
                        icons = folderStructure.isSelected("assets/minecraft/icons"),
                        lang = folderStructure.isSelected("assets/minecraft/lang"),
                        models = if (folderStructure.isSelected("assets/minecraft/models")) PackUtil.PackStructure.Minecraft.Models(
                            block = folderStructure.isSelected("assets/minecraft/models/block"),
                            item = folderStructure.isSelected("assets/minecraft/models/item")
                        ) else null,
                        particles = folderStructure.isSelected("assets/minecraft/particles"),
                        sounds = folderStructure.isSelected("assets/minecraft/sounds"),
                        shaders = if (folderStructure.isSelected("assets/minecraft/shaders")) PackUtil.PackStructure.Minecraft.Shaders(
                            post = folderStructure.isSelected("assets/minecraft/shaders/post"),
                            program = folderStructure.isSelected("assets/minecraft/shaders/program")
                        ) else null,
                        texts = folderStructure.isSelected("assets/minecraft/text"),
                        textures = if (folderStructure.isSelected("assets/minecraft/textures")) PackUtil.PackStructure.Minecraft.Textures(
                            block = folderStructure.isSelected("assets/minecraft/textures/block"),
                            colourmap = folderStructure.isSelected("assets/minecraft/textures/colourmap"),
                            effect = folderStructure.isSelected("assets/minecraft/textures/effect"),
                            entity = folderStructure.isSelected("assets/minecraft/textures/entity"),
                            environment = folderStructure.isSelected("assets/minecraft/textures/environment"),
                            font = folderStructure.isSelected("assets/minecraft/textures/font"),
                            gui = if (folderStructure.isSelected("assets/minecraft/textures/gui")) PackUtil.PackStructure.Minecraft.Textures.GUI(
                                advancements = if (folderStructure.isSelected("assets/minecraft/textures/gui/advancements")) PackUtil.PackStructure.Minecraft.Textures.GUI.Advancements(
                                    backgrounds = folderStructure.isSelected("assets/minecraft/textures/gui/advancements/backgrounds")
                                ) else null,
                                container = if (folderStructure.isSelected("assets/minecraft/textures/gui/container")) PackUtil.PackStructure.Minecraft.Textures.GUI.Container(
                                    creativeInventory = folderStructure.isSelected("assets/minecraft/textures/gui/container/creative_inventory")
                                ) else null,
                                presets = folderStructure.isSelected("assets/minecraft/textures/gui/container/presets"),
                                title = if (folderStructure.isSelected("assets/minecraft/textures/gui/title")) PackUtil.PackStructure.Minecraft.Textures.GUI.Title(
                                    background = folderStructure.isSelected("assets/minecraft/textures/gui/title/background")
                                ) else null
                            ) else null,
                            item = folderStructure.isSelected("assets/minecraft/textures/item"),
                            map = folderStructure.isSelected("assets/minecraft/textures/map"),
                            misc = folderStructure.isSelected("assets/minecraft/textures/misc"),
                            mobEffect = folderStructure.isSelected("assets/minecraft/textures/mob_effect"),
                            models = if (folderStructure.isSelected("assets/minecraft/textures/models")) PackUtil.PackStructure.Minecraft.Textures.Models(
                                armor = folderStructure.isSelected("assets/minecraft/textures/models/armor")
                            ) else null,
                            painting = folderStructure.isSelected("assets/minecraft/textures/painting"),
                            particle = folderStructure.isSelected("assets/minecraft/textures/particle")
                        ) else null
                    ) else null
                ) else null
            )
        )

        PackUtil.writeMcMeta(
            versions.selectedItem as Int,
            description
        )
        
        Quiver.resolution = resolutionEntry.text.toInt()
    }

    override fun validate(): Boolean = true
}