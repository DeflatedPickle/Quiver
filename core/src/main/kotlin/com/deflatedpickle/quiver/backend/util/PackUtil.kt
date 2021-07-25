/* Copyright (c) 2020-2021 DeflatedPickle under the MIT license */

@file:Suppress("unused")

package com.deflatedpickle.quiver.backend.util

import blue.endless.jankson.JsonObject
import com.deflatedpickle.marvin.Version
import com.deflatedpickle.marvin.VersionProgression
import com.deflatedpickle.marvin.dsl.cabinet
import com.deflatedpickle.marvin.dsl.dir
import com.deflatedpickle.marvin.dsl.file
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.Quiver.json
import com.github.underscore.lodash.U
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils
import java.io.File
import javax.imageio.ImageIO

object PackUtil {
    data class PackStructure(
        val packMcMeta: Boolean = true,
        val assets: Assets? = Assets()
    ) {
        data class Assets(
            val icons: Boolean = true,
            val minecraft: Minecraft? = Minecraft()
        )

        data class Minecraft(
            val soundsJson: Boolean = true,
            val blockstates: Boolean = true,
            val gpuWarnlistJson: Boolean = true,
            val font: Boolean = true,
            val icons: Boolean = true,
            val lang: Boolean = true,
            val models: Models? = Models(),
            val particles: Boolean = true,
            val sounds: Boolean = true,
            val shaders: Shaders? = Shaders(),
            val texts: Boolean = true,
            val textures: Textures? = Textures()
        ) {
            data class Models(
                val block: Boolean = true,
                val item: Boolean = true
            )

            data class Shaders(
                val post: Boolean = true,
                val program: Boolean = true
            )

            data class Textures(
                val block: Boolean = true,
                val colourmap: Boolean = true,
                val effect: Boolean = true,
                val entity: Boolean = true,
                val environment: Boolean = true,
                val font: Boolean = true,
                val gui: GUI? = GUI(),
                val item: Boolean = true,
                val map: Boolean = true,
                val misc: Boolean = true,
                val mobEffect: Boolean = true,
                val models: Models? = Models(),
                val painting: Boolean = true,
                val particle: Boolean = true
            ) {
                data class GUI(
                    val advancements: Advancements? = Advancements(),
                    val container: Container? = Container(),
                    val presets: Boolean = true,
                    val title: Title? = Title()
                ) {
                    data class Advancements(
                        val backgrounds: Boolean = true
                    )

                    data class Container(
                        val creativeInventory: Boolean = true
                    )

                    data class Title(
                        val background: Boolean = true
                    )
                }

                data class Models(
                    val armor: Boolean = true
                )
            }
        }
    }

    // This structure is mostly the same through different pack versions
    // I'm not sure of a good, maintainable way to describe these mild differences, though I did write a DSL for it
    // https://minecraft.gamepedia.com/Resource_Pack#Folder_structure
    // (02/05/2021) This testing of each file is quite verbose. Perhaps some kind of "checker" could be added to the FileBuilder?
    fun createEmptyPack(
        path: String,
        build: Boolean = true,
        ps: PackStructure = PackStructure()
    ) =
        cabinet(path, build) {
            if (ps.packMcMeta) file("pack.mcmeta") {
                +"{}"
            }
            if (ps.assets != null) dir("assets") {
                if (ps.assets.icons) dir("icons") {}
                if (ps.assets.minecraft != null) dir("minecraft") {
                    if (ps.assets.minecraft.soundsJson) file("sounds.json") {
                        +"{}"
                    }
                    if (ps.assets.minecraft.blockstates) dir("blockstates") {}
                    if (ps.assets.minecraft.gpuWarnlistJson) file("gpu_warnlist.json") {
                        +"{}"
                    }
                    if (ps.assets.minecraft.font) dir("font") {}
                    if (ps.assets.minecraft.icons) dir("icons") {}
                    if (ps.assets.minecraft.lang) dir("lang") {}
                    if (ps.assets.minecraft.models != null) dir("models") {
                        if (ps.assets.minecraft.models.block) dir("block") {}
                        if (ps.assets.minecraft.models.item) dir("item") {}
                    }
                    if (ps.assets.minecraft.particles) dir("particles") {}
                    if (ps.assets.minecraft.sounds) dir("sounds") {}
                    if (ps.assets.minecraft.shaders != null) dir("shaders") {
                        if (ps.assets.minecraft.shaders.post) dir("post") {}
                        if (ps.assets.minecraft.shaders.program) dir("program") {}
                    }
                    if (ps.assets.minecraft.texts) dir("texts") {}
                    if (ps.assets.minecraft.textures != null) dir("textures") {
                        if (ps.assets.minecraft.textures.block) dir("block") {}
                        if (ps.assets.minecraft.textures.colourmap) dir("colormap") {}
                        if (ps.assets.minecraft.textures.effect) dir("effect") {}
                        if (ps.assets.minecraft.textures.entity) dir("entity") {}
                        if (ps.assets.minecraft.textures.environment) dir("environment") {}
                        if (ps.assets.minecraft.textures.font) dir("font") {}
                        if (ps.assets.minecraft.textures.gui != null) dir("gui") {
                            if (ps.assets.minecraft.textures.gui.advancements != null) dir("advancements") {
                                if (ps.assets.minecraft.textures.gui.advancements.backgrounds) dir("backgrounds") {}
                            }
                            if (ps.assets.minecraft.textures.gui.container != null) dir("container") {
                                if (ps.assets.minecraft.textures.gui.container.creativeInventory) dir("creative_inventory") {}
                            }
                            if (ps.assets.minecraft.textures.gui.presets) dir("presets") {}
                            if (ps.assets.minecraft.textures.gui.title != null) dir("title") {
                                if (ps.assets.minecraft.textures.gui.title.background) dir("background") {}
                            }
                        }
                        if (ps.assets.minecraft.textures.item) dir("item") {}
                        if (ps.assets.minecraft.textures.map) dir("map") {}
                        if (ps.assets.minecraft.textures.misc) dir("misc") {}
                        if (ps.assets.minecraft.textures.mobEffect) dir("mob_effect") {}
                        if (ps.assets.minecraft.textures.models != null) dir("models") {
                            if (ps.assets.minecraft.textures.models.armor) dir("armor") {}
                        }
                        if (ps.assets.minecraft.textures.painting) dir("painting") {}
                        if (ps.assets.minecraft.textures.particle) dir("particle") {}
                    }
                }
            }
        }

    /**
     * Extracts all the resources included in a resource pack
     *
     * Note: For default packs, resources like sounds are not bundled, please use [extractExtraData] as well to get those
     */
    fun extractPack(
        /**
         * The JAR of a Minecraft version
         * ex; .minecraft/versions/1.16.2.jar
         */
        file: File,
        /**
         * The path were these resources will be placed
         */
        path: String
    ) {
        val zipFile = ZipFile(file).apply {
            // isRunInThread = true
        }

        for (
            i in zipFile.fileHeaders.filter {
                it.fileName.startsWith("assets/minecraft")
            }
        ) {
            if (!i.isDirectory) {
                zipFile.extractFile(
                    i.fileName,
                    path
                )
            }
        }
    }

    /**
     * Extracts sounds, extra lang's and icons
     */
    fun extractExtraData(
        /**
         * The JSON data of a Minecraft version
         * ex; .minecraft/versions/1.16.2.json
         */
        file: File,
        /**
         * The path were these resources will be placed
         */
        path: String,
        /**
         * The resource types to extract
         */
        vararg types: ExtraResourceType
    ) {
        // This gets the version of index we should use, e.g; 1.14, 1.16
        val version = json
            .load(file)
            .getObject("assetIndex")!!
            .get(String::class.java, "id")
        // .minecraft/assets/indexes/$version
        val indexFile = DotMinecraft.assetsIndexes.resolve("$version.json")
        // Loads indexFile as JSON and gets the "object" key
        val indexObject = json.load(indexFile).getObject("objects")!!

        for ((asset, jsonElement) in indexObject.entries) {
            // Gets the hash for the current asset
            val hash = (jsonElement as JsonObject).get(String::class.java, "hash")!!

            for (resourceType in types) {
                if (asset.startsWith(resourceType.path)) {
                    // Resolves to the source, using the hash
                    // The assets are in folders named the first 2 characters of the hash
                    // Not sure why that is
                    val source = DotMinecraft.assetsObjects.resolve(hash.substring(0..1)).resolve(hash)
                    // Gets the namespace to use from the resource type
                    val namespace = resourceType.path.split("/")[0]
                    // Constructs the destination, using the namespace and the assets path
                    val destination =
                        "$path/assets/$namespace/${asset.split("/").drop(1).joinToString(separator = "/")}"
                    // Copies the source file to the destination, creating all parent files
                    FileUtils.copyFile(source, File(destination).apply { parentFile.mkdirs() })
                }
            }
        }
    }

    /**
     * Writes the MCMeta data to the open pack
     */
    fun writeMcMeta(version: Int, description: String) {
        Quiver.packDirectory?.resolve("pack.mcmeta")?.writeText(
            // I don't know if that's proper formatting, so format it again
            U.formatJson(
                """
                        {
                            "pack": {
                                "pack_format": $version,
                                "description": "$description"
                            }
                        }
                """.trimIndent()
            )
        )
    }

    // You guys ready for some YandereDev-level code?

    fun packVersionToGameVersion(packVersion: PackFormat): VersionProgression =
        when (packVersion) {
            1 -> v1_6_1..v1_8_9
            2 -> v1_9_0..v1_10_2
            3 -> v1_11_0..v1_12_0
            4 -> v1_13_0..v1_14_4
            5 -> v1_15_0..v1_16_1
            6 -> v1_16_2..v1_16_4
            7 -> v1_17_0..v1_17_0
            else -> Version.ZERO..Version.ZERO
        }

    fun gameVersionToPackVersion(gameVersion: Version): PackFormat =
        when (gameVersion) {
            in v1_6_1..v1_8_9 -> 1
            in v1_9_0..v1_10_2 -> 2
            in v1_11_0..v1_12_0 -> 3
            in v1_13_0..v1_14_4 -> 4
            in v1_15_0..v1_16_1 -> 5
            in v1_16_2..v1_16_4 -> 6
            in v1_17_0..v1_17_0 -> 7
            else -> 0
        }

    // Awful, isn't it? At least I didn't chain if/else if

    fun findResolution(pack: File = Quiver.packDirectory!!): Int {
        val textures = pack.resolve("assets").resolve("minecraft").resolve("textures")
        for (type in arrayOf("item", "block")) {
            val sub = textures.resolve(type)
            if (sub.exists()) {
                for (i in sub.listFiles()!!) {
                    if (i.extension == "png") {
                        val image = ImageIO.read(i)

                        if (image.width == image.height) {
                            return image.width
                        }
                    }
                }
            }
        }

        return 0
    }
}

typealias PackFormat = Int
