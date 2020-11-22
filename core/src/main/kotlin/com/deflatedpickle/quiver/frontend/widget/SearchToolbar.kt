package com.deflatedpickle.quiver.frontend.widget

import org.jdesktop.swingx.JXSearchField
import org.jdesktop.swingx.search.Searchable
import java.util.regex.Pattern
import javax.swing.JCheckBox
import javax.swing.JSeparator
import javax.swing.JToolBar

class SearchToolbar(
    private val searchable: Searchable
) : JToolBar("Search") {
    private val searchField = JXSearchField("Search").apply {
        searchMode = JXSearchField.SearchMode.INSTANT
    }

    private val backwardsCheckbox = JCheckBox("Backwards")

    // Regex checks
    private val caseInsensitiveCheckbox = JCheckBox("Case Insensitive").apply {
        isSelected = true
    }
    private val ignoreWhitespaceCheckbox = JCheckBox("Ignore Whitespace").apply {
        isSelected = true
    }

    // Non-regex checks
    private val stripSpecialChecks = JCheckBox("Strip Special Characters")

    init {
        this.add(this.searchField)
        this.add(this.backwardsCheckbox)
        this.addSeparator()
        this.add(this.caseInsensitiveCheckbox)
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
                this.caseInsensitiveCheckbox.isSelected,
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