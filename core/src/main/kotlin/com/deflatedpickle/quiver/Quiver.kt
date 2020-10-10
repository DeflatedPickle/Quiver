/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.config.QuiverSettings

@Suppress("unused", "SpellCheckingInspection")
@Plugin(
    value = "quiver",
    author = "DeflatedPickle",
    version = "1.2.0",
    description = """
        <br>
        A program for creating Minecraft resource-packs
    """,
    type = PluginType.CORE_API,
    settings = QuiverSettings::class
)
object Quiver
