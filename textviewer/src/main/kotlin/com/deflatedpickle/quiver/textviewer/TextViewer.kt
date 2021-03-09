/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.quiver.backend.api.Viewer
import java.io.File
import javax.swing.JComponent
import javax.swing.JScrollPane
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane

object TextViewer : Viewer<File> {
    private val component = Component()

    override fun refresh(with: File) {
        this.component.text = with.readText()

        this.component.syntaxEditingStyle = when (with.extension) {
            // Do resource packs even have properties?
            "properties" -> SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE
            // I'm almost certain they use JSON, though!
            "json", "mcmeta" -> SyntaxConstants.SYNTAX_STYLE_JSON
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
            "lua" -> SyntaxConstants.SYNTAX_STYLE_LUA
            // Maybe people'll use a makefile?
            "make", "makefile", "makef", "gmk", "mak" -> SyntaxConstants.SYNTAX_STYLE_MAKEFILE
            // Gradle stuff for whatever reason
            "gradle" -> SyntaxConstants.SYNTAX_STYLE_GROOVY
            // OpenGL shaders and that
            // There isn't a syntax style for shaders so we'll use the closest
            "fsh", "vsh" -> SyntaxConstants.SYNTAX_STYLE_C
            // I declare anything else as plain text
            // Or so it was written, which it is
            else -> SyntaxConstants.SYNTAX_STYLE_NONE
        }
    }

    override fun getComponent(): JComponent = this.component
    override fun getScroller(): JScrollPane = RTextScrollPane(this.getComponent())
}
