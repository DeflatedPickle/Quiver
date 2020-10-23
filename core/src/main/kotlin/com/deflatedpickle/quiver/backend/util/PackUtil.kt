/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.util

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import com.deflatedpickle.marvin.builder.FileBuilder
import com.github.underscore.lodash.U
import java.io.File
import net.lingala.zip4j.ZipFile
import org.apache.commons.io.FileUtils

object PackUtil {
    private val json = Jankson.builder().build()

    // https://minecraft.gamepedia.com/Resource_Pack#Folder_structure
    fun createEmptyPack(path: String) {
        FileBuilder(path)
            .file("pack.mcmeta")
            .dir("assets")
            /*  */.dir("icons").build()
            /*  */.dir("minecraft")
            /*      */.file("sounds.json")
            /*      */.dir("blockstates").build()
            /*      */.file("gpu_warnlist.json")
            /*      */.dir("font").build()
            /*      */.dir("icons").build()
            /*      */.dir("lang").build()
            /*      */.dir("models")
            /*          */.dir("block").build()
            /*          */.dir("item").build()
            /*      */.build()
            /*      */.dir("particles").build()
            /*      */.dir("sounds").build()
            /*      */.dir("shaders")
            /*          */.dir("post").build()
            /*          */.dir("program").build()
            /*      */.build()
            /*      */.dir("texts").build()
            /*      */.dir("textures")
            /*          */.dir("block").build()
            /*          */.dir("colormap").build()
            /*          */.dir("effect").build()
            /*          */.dir("entity").build()
            /*          */.dir("environment").build()
            /*          */.dir("font").build()
            /*          */.dir("gui")
            /*              */.dir("advancements")
            /*                  */.dir("backgrounds").build()
            /*              */.build()
            /*              */.dir("container")
            /*                  */.dir("creative_inventory")
            /*              */.build()
            /*              */.dir("presets").build()
            /*              */.dir("title")
            /*                  */.dir("background").build()
            /*              */.build()
            /*          */.build()
            /*          */.dir("item").build()
            /*          */.dir("map").build()
            /*          */.dir("misc").build()
            /*          */.dir("mob_effect").build()
            /*          */.dir("models")
            /*              */.dir("armor").build()
            /*          */.build()
            /*          */.dir("painting").build()
            /*          */.dir("particle")
            /*      */.build()
            /*      */
            /*  */.build()
            .build()
            .build()
    }

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
        val zipFile = ZipFile(file)

        // TODO: Extract default packs in a secondary thread, making use of zip4j's ProgressMonitor
        for (i in zipFile.fileHeaders.filter {
            it.fileName.startsWith("assets/minecraft")
        }) {
            zipFile.extractFile(
                i,
                path
            )
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
        val version = this.json
            .load(file)
            .getObject("assetIndex")!!
            .get(String::class.java, "id")
        // .minecraft/assets/indexes/$version
        val indexFile = DotMinecraft.assetsIndexes.resolve("$version.json")
        // Loads indexFile as JSON and gets the "object" key
        val indexObject = this.json.load(indexFile).getObject("objects")!!

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
                    val destination = "$path\\assets\\$namespace\\${asset.split("/").drop(1).joinToString(separator = "\\")}"
                    // Copies the source file to the destination, creating all parent files
                    FileUtils.copyFile(source, File(destination).apply { parentFile.mkdirs() })
                }
            }
        }
    }

    fun writeMcMeta(version: Int, description: String) {
        DocumentUtil.current!!.resolve("pack.mcmeta").writeText(
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

    fun packVersionToGameVersion(packVersion: Int): VersionProgression =
        when (packVersion) {
            1 -> Version(1, 6, 1)..Version(1, 8, 9)
            2 -> Version(1, 9)..Version(1, 10, 2)
            3 -> Version(1, 11)..Version(1, 12, 2)
            4 -> Version(1, 13)..Version(1, 14, 4)
            5 -> Version(1, 15)..Version(1, 16, 1)
            6 -> Version(1, 16, 2)..Version(1, 16, 3)
            else -> VersionProgression(Version(0, 0), Version(0, 0))
        }

    fun gameVersionToPackVersion(gameVersion: Version): Int =
        when (gameVersion) {
            in Version(1, 6, 1)..Version(1, 8, 9) -> 1
            in Version(1, 9)..Version(1, 10, 2) -> 2
            in Version(1, 11)..Version(1, 12, 2) -> 3
            in Version(1, 13)..Version(1, 14, 4) -> 4
            in Version(1, 15)..Version(1, 16, 1) -> 5
            in Version(1, 16, 2)..Version(1, 16, 3) -> 6
            else -> 0
        }

    // Awful, isn't it? At least I didn't chain if/else if
}
