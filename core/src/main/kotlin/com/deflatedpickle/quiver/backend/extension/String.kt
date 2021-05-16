/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.extension

import org.fife.ui.rsyntaxtextarea.SyntaxConstants

fun String.toSyntaxEditingStyle() = when (this) {
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
