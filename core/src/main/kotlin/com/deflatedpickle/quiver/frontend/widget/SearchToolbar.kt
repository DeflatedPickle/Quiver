package com.deflatedpickle.quiver.frontend.widget

import com.deflatedpickle.nagato.NagatoIcon
import org.jdesktop.swingx.JXSearchField
import org.jdesktop.swingx.search.Searchable
import java.util.regex.Pattern
import javax.swing.*

class SearchToolbar(
    private val searchable: Searchable
) : JToolBar("Search") {
    private val searchField = JXSearchField("Search").apply {
        searchMode = JXSearchField.SearchMode.INSTANT
    }

    private val backwardsCheckbox = JToggleButton(NagatoIcon.UNDO).apply {
        toolTipText = "Backwards"
    }

    // Regex checks
    private val matchCaseCheckbox = JToggleButton(NagatoIcon.CASE_INSENSITIVE).apply {
        toolTipText = "Match Case"
    }
    private val ignoreWhitespaceCheckbox = JToggleButton(NagatoIcon.IGNORE_WHITESPACE).apply {
        toolTipText = "Ignore Whitespace"
        isSelected = true
    }

    // Non-regex checks
    private val stripSpecialChecks = JToggleButton(NagatoIcon.STRIP_SPECIAL_CHARACTERS).apply {
        toolTipText = "Strip Special Characters"
        isSelected = true
    }

    init {
        this.add(this.searchField)
        this.add(this.backwardsCheckbox)
        this.addSeparator()
        this.add(this.matchCaseCheckbox)
        this.add(this.ignoreWhitespaceCheckbox)
        this.addSeparator()
        this.add(this.stripSpecialChecks)

        this.searchField.addActionListener { it ->
            var search = it.actionCommand

            if (this.stripSpecialChecks.isSelected) {
                search = search.filter { it.isLetterOrDigit() }
            }

            this.search(
                search,
                !this.matchCaseCheckbox.isSelected,
                this.ignoreWhitespaceCheckbox.isSelected
            )
        }
    }

    private fun search(
        string: String,
        caseInsensitive: Boolean = true,
        comments: Boolean = false
    ): Int =
        this.searchable.search(
            Pattern.compile(
                "${
                    if (caseInsensitive) "(?i)" else ""
                }${
                    if (comments) "(?x)" else ""
                }$string", Pattern.CANON_EQ
            ),
            -1,
            this.backwardsCheckbox.isSelected
        )
}