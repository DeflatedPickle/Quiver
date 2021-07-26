/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filewatcher

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.event.EventProgramShutdown
import com.deflatedpickle.quiver.backend.event.EventOpenPack
import com.deflatedpickle.quiver.filewatcher.event.EventFileSystemUpdate
import io.methvin.watcher.DirectoryWatcher
import org.apache.logging.log4j.LogManager
import java.io.IOException

@Suppress("unused")
@Plugin(
    value = "$[name]",
    author = "$[author]",
    version = "$[version]",
    description = """
        <br>
        Watches files for changes and sends events to update widgets
    """,
    type = PluginType.API,
)
object FileWatcherPlugin {
    private val logger = LogManager.getLogger()

    private var watcher: DirectoryWatcher? = null

    init {
        EventProgramShutdown.addListener {
            watcher?.close()
        }

        EventOpenPack.addListener { pack ->
            try {
                watcher?.close()

                watcher = DirectoryWatcher.builder()
                    .path(pack.toPath())
                    .listener { event ->
                        // println("${event.path()}, ${event.isDirectory}")
                        EventFileSystemUpdate.trigger(event.path().toFile())
                    }
                    .build()
                watcher!!.watchAsync()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
