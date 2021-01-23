/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.util

import com.deflatedpickle.marvin.util.OSUtil
import io.github.erayerdin.kappdirs.AppDirsFactory
import java.io.File

object DotMinecraft {
    private val appDirs = AppDirsFactory.getInstance()

    val dotMinecraftPath = when {
        OSUtil.isWindows() -> appDirs.getUserConfigDir(
            ".minecraft",
            "",
            null,
            true
        )
        // Oh, great
        // It's somewhere special on Unix
        else -> File("${System.getProperty("user.home")}/.minecraft").toPath()
        // Yay!
    }

    val dotMinecraft = dotMinecraftPath.toFile()
    val versions = dotMinecraft.resolve("versions")

    val assets = dotMinecraft.resolve("assets")
    val assetsIndexes = assets.resolve("indexes")
    val assetsObjects = assets.resolve("objects")
}
