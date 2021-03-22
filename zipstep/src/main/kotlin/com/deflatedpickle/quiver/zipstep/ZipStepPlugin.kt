/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.zipstep

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.api.registry.Registry
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.packexport.api.ExportStep

@Suppress("unused")
@Plugin(
    value = "zip_step",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A pack export step to put the pack in a ZIP
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@pack_export#>=1.0.0"
    ],
    settings = ZipStepSettings::class
)
object ZipStepPlugin {
    init {
        EventProgramFinishSetup.addListener {
            val exportStepRegistry = RegistryUtil.get("export")

            @Suppress("UNCHECKED_CAST")
            (exportStepRegistry as Registry<String, ExportStep>?)?.register("zip", ZipStep)
        }
    }
}
