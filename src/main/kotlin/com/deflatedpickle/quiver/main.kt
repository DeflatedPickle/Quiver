package com.deflatedpickle.quiver

import com.deflatedpickle.quiver.frontend.window.Window
import org.apache.logging.log4j.LogManager
import org.oxbow.swingbits.dialog.task.TaskDialogs
import java.awt.Dimension
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

    System.setProperty("log4j.skipJansi", "false")
    val logger = LogManager.getLogger()

    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        logger.warn("${t.name} threw $e")
        SwingUtilities.invokeLater {
            TaskDialogs
                .build()
                .parent(Window)
                .showException(e)
        }
    }
    logger.info("Registered a default exception handler")

    SwingUtilities.invokeLater {
        Window.size = Dimension(800, 600)
        Window.setLocationRelativeTo(null)

        Window.control.contentArea.deploy(Window.grid)

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        SwingUtilities.updateComponentTreeUI(Window)

        Window.isVisible = true
    }
}