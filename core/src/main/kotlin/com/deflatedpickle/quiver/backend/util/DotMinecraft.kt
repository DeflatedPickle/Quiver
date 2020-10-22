package com.deflatedpickle.quiver.backend.util

import io.github.erayerdin.kappdirs.AppDirsFactory

object DotMinecraft {
    private val appDirs = AppDirsFactory.getInstance()

    val dotMinecraftPath = appDirs.getUserConfigDir(
        ".minecraft",
        "",
        null,
        true
    )

    val dotMinecraft = dotMinecraftPath.toFile()
    val versions = dotMinecraft.resolve("versions")

    val assets = dotMinecraft.resolve("assets")
    val assetsIndexes = assets.resolve("indexes")
    val assetsObjects = assets.resolve("objects")
}