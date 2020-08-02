package com.deflatedpickle.quiver.backend.util

import net.harawata.appdirs.AppDirsFactory
import java.io.File

object DotMinecraft {
    private val appDirs = AppDirsFactory.getInstance()
    val dotMinecraftString = appDirs.getUserDataDir(".minecraft", null, null, true)

    val dotMinecraft = File(dotMinecraftString)
    val versions = dotMinecraft.resolve("versions")
}