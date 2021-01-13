package com.deflatedpickle.quiver.packsquashstep

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.registry.Registry
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.packexport.api.ExportStep

@Plugin(
    value = "pack_squash_step",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A pack export step that utilizes PackSquash to decrease file sizes
    """,
    type = PluginType.API,
    settings = PackSquashStepSettings::class
)
object PackSquashStepPlugin {
    init {
        EventProgramFinishSetup.addListener {
            val exportStepRegistry = RegistryUtil.get("export")

            @Suppress("UNCHECKED_CAST")
            (exportStepRegistry as Registry<String, ExportStep>?)?.register("packsquash", PackSquashStep)
        }
    }
}