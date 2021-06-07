/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.frontend.widget

import com.athaydes.kunion.Union
import com.deflatedpickle.monocons.MonoIcon
import com.deflatedpickle.undulation.DocumentAdapter
import com.deflatedpickle.undulation.extensions.getText
import java.util.regex.Pattern
import javax.swing.JToggleButton
import javax.swing.JToolBar
import org.jdesktop.swingx.JXButton
import org.jdesktop.swingx.JXSearchField
import org.jdesktop.swingx.JXTable
import org.jdesktop.swingx.JXTree

class SearchToolbar(
    private val widgetUnion: Union.U2<JXTree, JXTable>
) : JToolBar("Search") {
    private val searchField: JXSearchField = JXSearchField("Search").apply {
        searchMode = JXSearchField.SearchMode.INSTANT

        document.addDocumentListener(DocumentAdapter {
            val search = processSearchString(it.document.getText(), stripSpecialChecks.isSelected)

            search.isNotBlank().apply {
                previousButton.isEnabled = this
                nextButton.isEnabled = this

                search(
                    search,
                    !matchCaseCheckbox.isSelected,
                    ignoreWhitespaceCheckbox.isSelected
                )
            }
        })
    }

    private val previousButton: JXButton = JXButton(MonoIcon.ARROW_UP).apply {
        toolTipText = "Previous"
        isEnabled = false

        addActionListener {
            search(
                processSearchString(searchField.text, stripSpecialChecks.isSelected),
                !matchCaseCheckbox.isSelected,
                ignoreWhitespaceCheckbox.isSelected,
                widgetUnion.use(
                    { tree -> tree.selectionModel.maxSelectionRow - 1 },
                    { table -> table.selectionModel.maxSelectionIndex - 1 }
                ),
                true
            )
        }
    }

    private val nextButton = JXButton(MonoIcon.ARROW_DOWN).apply {
        toolTipText = "Next"
        isEnabled = false

        addActionListener {
            search(
                processSearchString(searchField.text, stripSpecialChecks.isSelected),
                !matchCaseCheckbox.isSelected,
                ignoreWhitespaceCheckbox.isSelected,
                widgetUnion.use(
                    { tree -> tree.selectionModel.maxSelectionRow + 1 },
                    { table -> table.selectionModel.maxSelectionIndex + 1 }
                )
            )
        }
    }

    private val loopButton = JToggleButton(MonoIcon.RELOAD).apply {
        toolTipText = "Loop"
        isSelected = true
    }

    // Regex checks
    private val matchCaseCheckbox = JToggleButton(MonoIcon.CASE_INSENSITIVE).apply {
        toolTipText = "Match Case"
    }
    private val ignoreWhitespaceCheckbox = JToggleButton(MonoIcon.IGNORE_WHITESPACE).apply {
        toolTipText = "Ignore Whitespace"
        isSelected = true
    }

    // Non-regex checks
    private val stripSpecialChecks = JToggleButton(MonoIcon.STRIP_SPECIAL_CHARACTERS).apply {
        toolTipText = "Strip Special Characters"
        isSelected = true
    }

    init {
        this.add(this.searchField)
        this.addSeparator()
        this.add(this.previousButton)
        this.add(this.nextButton)
        this.add(this.loopButton)
        this.addSeparator()
        this.add(this.matchCaseCheckbox)
        this.add(this.ignoreWhitespaceCheckbox)
        this.addSeparator()
        this.add(this.stripSpecialChecks)
    }

    private fun processSearchString(string: String, ignoreSpecialChecks: Boolean): String {
        var new = string

        if (ignoreSpecialChecks) {
            new = new.filter { it.isLetterOrDigit() }
        }

        return new
    }

    private fun search(
        string: String,
        caseInsensitive: Boolean = true,
        comments: Boolean = false,
        start: Int = -1,
        backwards: Boolean = false
    ) {
        val pattern = Pattern.compile(
            "${
                if (caseInsensitive) "(?i)" else ""
            }${
                if (comments) "(?x)" else ""
            }$string", Pattern.CANON_EQ
        )

        val searchable = this.widgetUnion.use(
            { it.searchable },
            { it.searchable }
        )

        if (backwards) {
            val trySearch = searchable.search(
                pattern,
                start,
                true
            )

            if (loopButton.isSelected && trySearch == -1) {
                searchable.search(
                    pattern,
                    -1,
                    true
                )
            }
        } else {
            val trySearch = searchable.search(
                pattern,
                start,
                false
            )

            if (loopButton.isSelected && trySearch == -1) {
                searchable.search(
                    pattern,
                    -1,
                    false
                )
            }
        }
    }
}
