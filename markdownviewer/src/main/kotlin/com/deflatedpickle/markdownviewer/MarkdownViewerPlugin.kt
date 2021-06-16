/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.markdownviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.filepanel.api.Viewer

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        A viewer for Markdown files
    """,
    type = PluginType.OTHER,
    dependencies = [
        "deflatedpickle@file_panel#>=1.0.0"
    ]
)
object MarkdownViewerPlugin {
    // https://superuser.com/a/285878
    private val extensionSet = setOf(
        "markdown",
        "mdown",
        "mkdn",
        "md",
        "mkd",
        "mkwn",
        "mdtxt",
        "mdtext",
        // "text",
        "Rmd"
    )

    init {
        EventProgramFinishSetup.addListener {
            val registry = RegistryUtil.get("viewer") as Registry<String, MutableList<Viewer<Any>>>?

            if (registry != null) {
                for (i in this.extensionSet) {
                    if (registry.get(i) == null) {
                        registry.register(i, mutableListOf(MarkdownViewer as Viewer<Any>))
                    } else {
                        registry.get(i)!!.add(MarkdownViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}
