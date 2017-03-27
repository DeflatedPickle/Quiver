#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""A subclass of the Text widget with functions for highlighting text."""

import tkinter as tk

# Credit: Bryan Oakley
# http://stackoverflow.com/questions/3781670/how-to-highlight-text-in-a-tkinter-text-widget


class HighlightingText(tk.Text):
    """A text widget with a new method, highlight_pattern()

    example:

    text = CustomText()
    text.tag_configure("red", foreground="#ff0000")
    text.highlight_pattern("this should be red", "red")

    The highlight_pattern method is a simplified python
    version of the tcl code at http://wiki.tcl.tk/3246
    """

    def __init__(self, *args, **kwargs):
        tk.Text.__init__(self, *args, **kwargs)

    def highlight_pattern(self, pattern, tag, start="1.0", end="end",
                          regexp=False):
        """Apply the given tag to all text that matches the given pattern

        If 'regexp' is set to True, pattern will be treated as a regular
        expression according to Tcl's regular expression syntax.
        """

        start = self.index(start)
        end = self.index(end)
        self.mark_set("matchStart", start)
        self.mark_set("matchEnd", start)
        self.mark_set("searchLimit", end)

        count = tk.IntVar()
        while True:
            index = self.search(pattern, "matchEnd", "searchLimit", count=count, regexp=regexp)
            if index == "": break
            if count.get() == 0: break  # degenerate pattern which matches zero-length strings
            self.mark_set("matchStart", index)
            self.mark_set("matchEnd", "%s+%sc" % (index, count.get()))
            self.tag_add(tag, "matchStart", "matchEnd")

    def highlight_until(self, pattern, stop, tag, end="end"):
        first = 1.0
        while True:
            first = self.search(pattern, first, end)
            if not first:
                break
            # last = first + "+" + str(len(pattern)) + "c"
            next = self.search(stop, first + "+1c", end)
            if next == "":
                next = self.search("\n", first, end)
            last = next
            if self.get(first) == "§":
                self.tag_add("section sign", first, first + "+2c")
            self.tag_add(tag, first + "+0c", last)
            first = last
