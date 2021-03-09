package com.deflatedpickle.markdownviewer

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramFinishSetup
import com.deflatedpickle.haruhi.util.RegistryUtil
import com.deflatedpickle.quiver.backend.api.Viewer

@Suppress("unused")
@Plugin(
    value = "markdown_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A viewer for Markdown files
    """,
    type = PluginType.OTHER
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
                    val ext = """.*\.$i"""

                    if (registry.get(ext) == null) {
                        registry.register(ext, mutableListOf(MarkdownViewer as Viewer<Any>))
                    } else {
                        registry.get(ext)!!.add(MarkdownViewer as Viewer<Any>)
                    }
                }
            }
        }
    }
}