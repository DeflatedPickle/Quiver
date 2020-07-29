package com.deflatedpickle.quiver.launcher

import com.deflatedpickle.haruhi.api.plugin.DependencyComparator
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.haruhi.component.PluginPanel
import com.deflatedpickle.haruhi.util.ClassGraphUtil
import com.deflatedpickle.haruhi.util.PluginUtil
import com.deflatedpickle.quiver.backend.event.EventCreateFile
import com.deflatedpickle.quiver.frontend.window.Window
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Dimension
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main(args: Array<String>) {
    // We set the LaF now so any error pop-ups use the use it
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    // Setting this property gives us terminal colours
    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger()

    // The gradle tasks pass in "indev" argument
    // if it doesn't exist it's not indev
    PluginUtil.isInDev = args.contains("indev")
    PluginUtil.control = Window.control

    // Handle all uncaught exceptions to open a pop-up
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.warn("${t.name} threw $e")
        // We'll invoke it on the Swing thread
        // This will wait at least for the window to open first
        SwingUtilities.invokeLater {
            // Open a dialog to report the error to the user
            TaskDialogs
                .build()
                .parent(Window)
                .showException(e)
        }
    }
    logger.info("Registered a default exception handler")

    // Plugins are distributed and loaded as JARs
    // when the program is built
    if (!PluginUtil.isInDev) {
        EventCreateFile.trigger(
            PluginUtil.createPluginsFolder().apply {
                logger.info("Created the plugins folder at ${this.absolutePath}")
            }
        )
    }

    // Start a scan of the class graph
    // this will discover all plugins
    ClassGraphUtil.refresh()

    // Finds all singletons extending Plugin
    PluginUtil.discoverPlugins()
    logger.debug("Validated all plugins with ${PluginUtil.unloadedPlugins.size} error/s")

    // Organise plugins by their dependencies
    PluginUtil.discoveredPlugins.sortWith(DependencyComparator)
    logger.info("Sorted out the load order: ${PluginUtil.discoveredPlugins.map { PluginUtil.pluginToSlug(it) }}")
    // EventSortedPluginLoadOrder.trigger(PluginUtil.discoveredPlugins)

    // Loads all classes with a Plugin annotation
    PluginUtil.loadPlugins {
        // Validate all the small things

        // Versions must be semantic
        PluginUtil.validateVersion(it) &&
                // Descriptions must contain a <br> tag
                PluginUtil.validateDescription(it) &&
                // Specific types need a specified field
                PluginUtil.validateType(it) &&
                // Dependencies should be "author@plugin#version"
                PluginUtil.validateDependencySlug(it) &&
                // The dependency should exist
                PluginUtil.validateDependencyExistence(it)
    }
    logger.info("Loaded plugins; ${PluginUtil.loadedPlugins.map { PluginUtil.pluginToSlug(it) }}")
    // EventLoadedPlugins.trigger(PluginUtil.loadedPlugins)

    val componentList = mutableListOf<PluginPanel>()
    for (plugin in PluginUtil.discoveredPlugins) {
        if (plugin.component != Nothing::class) {
            with(plugin.component.objectInstance!!) {
                PluginUtil.createComponent(plugin, this)
                componentList.add(this)
                // EventCreatePluginComponent.trigger(this)
            }
        }
    }
    // EventCreatedPluginComponents.trigger(componentList)

    SwingUtilities.invokeLater {
        Window.size = Dimension(800, 600)
        Window.setLocationRelativeTo(null)

        Window.control.contentArea.deploy(Window.grid)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)

        Window.isVisible = true
    }
}