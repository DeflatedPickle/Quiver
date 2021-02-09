/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.packsquashstep

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.registry.Registry
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.packexport.api.ExportStep

@Suppress("unused")
@Plugin(
    value = "pack_squash_step",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A pack export step that utilizes PackSquash to decrease file sizes
    """,
    type = PluginType.OTHER,
    settings = PackSquashStepSettings::class
)
object PackSquashStepPlugin {
    val processList = mutableListOf<Process>()

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
            for (i in processList) {
                if (i.isAlive) {
                    i.destroyForcibly()
                }
            }
        })

        EventProgramFinishSetup.addListener {
            val exportStepRegistry = RegistryUtil.get("export")

            @Suppress("UNCHECKED_CAST")
            (exportStepRegistry as Registry<String, ExportStep>?)?.register("packsquash", PackSquashStep)
        }
    }
}
