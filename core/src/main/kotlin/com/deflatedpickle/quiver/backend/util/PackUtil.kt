package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.marvin.builder.FileBuilder
import com.github.underscore.lodash.U
import net.lingala.zip4j.ZipFile
import java.io.File

object PackUtil {
    // https://minecraft.gamepedia.com/Resource_Pack#Folder_structure
    fun createEmptyPack(path: String, namespace: String) {
        FileBuilder(path)
            .file("pack.mcmeta")
            .dir("assets")
            /*  */.dir("icons").build()
            /*  */.dir(namespace)
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

    fun extractPack(file: File, path: String, namespace: String) {
        val zipFile = ZipFile(file)

        // TODO: Extract default packs in a secondary thread, making use of zip4j's ProgressMonitor
        for (i in zipFile.fileHeaders.filter {
            it.fileName.startsWith("assets/minecraft")
        }) {
            zipFile.extractFile(
                i,
                "$path\\assets\\${namespace}"
            )
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

    // Awful, isn't it?
}