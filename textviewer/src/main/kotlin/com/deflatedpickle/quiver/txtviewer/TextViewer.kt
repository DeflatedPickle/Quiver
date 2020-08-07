package com.deflatedpickle.quiver.txtviewer

import com.deflatedpickle.haruhi.api.plugin.Plugin
import com.deflatedpickle.haruhi.api.plugin.PluginType
import com.deflatedpickle.quiver.filepanel.event.EventChangeViewWidget
import com.deflatedpickle.rawky.ui.constraints.FillBothFinishLine
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import javax.swing.JScrollPane

@Suppress("unused")
@Plugin(
    value = "txt_viewer",
    author = "DeflatedPickle",
    version = "1.0.0",
    description = """
        <br>
        A TXT viewer for the file_panel widget
    """,
    type = PluginType.OTHER
)
object TextViewer {
    private val extensionSet = setOf(
        "txt",
        "json",
        "properties",
        "js", "ts",
        "gitignore",
        "md",
        "bat", "py", "rb",
        "gradle"
    )

    private val scroller = RTextScrollPane(Viewer)

    init {
        EventChangeViewWidget.addListener {
            if (it.first.extension in this.extensionSet) {
                Viewer.text = it.first.readText()

                Viewer.syntaxEditingStyle = when (it.first.extension) {
                    // I'm almost certain they use JSON, though!
                    "json" -> SyntaxConstants.SYNTAX_STYLE_JSON
                    // Do resource packs even have properties?
                    "properties" -> SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE
                    // I think resource packs can have scripts?
                    "js" -> SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT
                    // And if people don't want to use JavaScript...
                    "ts" -> SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT
                    // There's no stock "ignore" or "markdown" file highlighter
                    // So we'll use a random language with "#" comments
                    "gitignore", "md" -> SyntaxConstants.SYNTAX_STYLE_PERL
                    // In case people use scripts?
                    "bat" -> SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH
                    "py" -> SyntaxConstants.SYNTAX_STYLE_PYTHON
                    "rb" -> SyntaxConstants.SYNTAX_STYLE_RUBY
                    // Gradle stuff for whatever reason
                    "gradle" -> SyntaxConstants.SYNTAX_STYLE_GROOVY
                    // I declare anything else as plain text
                    // Or so it was written
                    else -> SyntaxConstants.SYNTAX_STYLE_NONE
                }

                it.second.add(this.scroller, FillBothFinishLine)
                it.second.repaint()
                it.second.revalidate()
            } else if (Viewer.text != "") {
                Viewer.text = ""

                it.second.repaint()
                it.second.revalidate()
            }
        }
    }
}