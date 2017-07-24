#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The text-editor for Quiver."""

import tkinter as tk
from _tkinter import TclError
from tkinter import ttk
import sys

import pkinter as pk

import load_images


class TextEditor(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Redstone")
        image = load_images.LoadImages()
        self.geometry("600x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        self.transient(parent)
        self.rowconfigure(2, weight=1)
        self.columnconfigure(0, weight=1)

        self.image_save = image.image_save

        self.image_refresh = image.image_refresh

        self.image_find = image.image_find
        self.image_find_next = image.image_find_next
        self.image_find_previous = image.image_find_previous

        self.image_replace = image.image_replace

        self.image_cut = image.image_cut
        self.image_copy = image.image_copy
        self.image_paste = image.image_paste
        self.image_delete = image.image_delete

        self.image_undo = image.image_undo
        self.image_redo = image.image_redo

        self.image_exit = image.image_exit

        self.file = None

        self.previous = 1.0
        self.stop_searching = False
        self.stop_replacing = False

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.widget_frame_search = ttk.Frame(self)

        self.findbar = Findbar(self.widget_frame_search)
        self.replacebar = Replacebar(self.widget_frame_search)

        self.statusbar = Statusbar(self)
        self.statusbar.grid(row=3, column=0, sticky="we")

        ##################################################

        self.widget_frame_code = ttk.Frame(self)
        self.widget_frame_code.grid(row=2, column=0, sticky="nesw")
        self.widget_frame_code.rowconfigure(0, weight=1)
        self.widget_frame_code.columnconfigure(1, weight=1)

        self.widget_text_code = tk.Text(self.widget_frame_code, wrap="none", undo=True, width=0, height=0)
        self.widget_text_code.grid(row=0, column=1, sticky="nesw")
        self.widget_text_code.focus_set()

        self.widget_text_code.bind("<KeyRelease>", self.statusbar.check_caret, "+")
        self.widget_text_code.bind("<ButtonRelease>", self.statusbar.check_caret, "+")
        self.statusbar.check_caret()

        self.widget_text_code.tag_configure("search", background="lime")
        self.widget_text_code.tag_raise("sel")

        self.widget_scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_code, orient="horizontal",
                                                         command=self.widget_text_code.xview)
        self.widget_scrollbar_horizontal.grid(row=1, column=1, sticky="we")

        self.widget_scrollbar_vertical = ttk.Scrollbar(self.widget_frame_code, orient="vertical",
                                                       command=self.widget_text_code.yview)
        self.widget_scrollbar_vertical.grid(row=0, column=2, sticky="ns")

        self.line_numbers = pk.LineNumbers(self.widget_frame_code, text_widget=self.widget_text_code,
                                           scroll_widget=self.widget_scrollbar_vertical)
        self.line_numbers.grid(row=0, column=0, sticky="ns")

        self.widget_text_code.configure(xscrollcommand=self.widget_scrollbar_horizontal.set,
                                        yscrollcommand=self.widget_scrollbar_vertical.set)

    def load_file(self, file=""):
        self.file = file

        with open(file, "r") as f:
            self.widget_text_code.delete(1.0, "end")
            self.widget_text_code.insert(1.0, f.read())
            self.title("{} - {}".format("Redstone", self.file))
            f.close()

    def save(self):
        with open(self.file, "w") as f:
            f.write(self.widget_text_code.get(1.0, "end"))
            f.close()

    def reload(self):
        self.load_file(self.file)

    def cut(self):
        self.copy()
        self.delete()

    def copy(self):
        self.widget_text_code.clipboard_clear()

        try:
            self.widget_text_code.clipboard_append(self.widget_text_code.get("sel.first", "sel.last"))
        except TclError:
            pass

    def paste(self):
        try:
            self.widget_text_code.insert("insert", self.widget_text_code.selection_get(selection="CLIPBOARD"))
        except TclError:
            pass

    def delete(self):
        try:
            self.widget_text_code.delete("sel.first", "sel.last")
        except TclError:
            pass

    def undo(self):
        try:
            self.widget_text_code.edit_undo()
            self.line_numbers._redraw()
        except TclError:
            pass

    def redo(self):
        try:
            self.widget_text_code.edit_redo()
            self.line_numbers._redraw()
        except TclError:
            pass

    def open_findbar(self):
        self.stop_searching = False
        self.stop_replacing = False

        if self.toolbar.variable_find.get():
            self.widget_frame_search.grid(row=1, column=0, sticky="ew")
            self.findbar.grid(row=0, column=0, sticky="ew")
            self.findbar.search_entry.entry.focus_set()

        elif self.toolbar.variable_replace.get():
            self.toolbar.variable_find.set(True)

        else:
            self.widget_frame_search.grid_remove()
            self.findbar.grid_remove()
            self.previous = 1.0
            self.widget_text_code.tag_remove("sel", "1.0", "end")

            search = self.widget_text_code.tag_nextrange("search", "1.0", "end")
            try:
                self.widget_text_code.tag_add("sel", search[0], search[1])
                self.widget_text_code.mark_set("insert", search[1])
            except IndexError:
                pass

            self.widget_text_code.focus_set()

            self.widget_text_code.tag_remove("search", "1.0", "end")
            self.findbar.search_entry.variable_search.set(False)

    def open_replacebar(self):
        self.stop_searching = False
        self.stop_replacing = False

        if self.toolbar.variable_replace.get():
            self.toolbar.variable_find.set(True)
            self.open_findbar()
            self.replacebar.grid(row=1, column=0, sticky="ew")

        else:
            # self.open_findbar()
            self.replacebar.grid_remove()

    def search(self, all_=None, next_=None, previous=None, match_case=None, exact=None, regular_expression=None):
        if not all_:
            self.widget_text_code.tag_remove("search", "1.0", "end")

        if not next_ and not previous:
            self.findbar.search_entry.variable_search.set(True)

        variable_search = tk.StringVar()

        if self.previous == "":
            self.previous = 1.0
            self.stop_searching = True

        position = self.widget_text_code.search(self.findbar.search_entry.entry.get(), self.previous if next_ else 1.0 if previous else 1.0, stopindex="end", forwards=next_, backwards=previous, nocase=match_case, exact=exact, regexp=regular_expression, count=variable_search)

        try:
            self.widget_text_code.tag_add("search", position, "{} + {} chars".format(position, variable_search.get()))
            self.widget_text_code.see(position)
        except TclError:
            self.previous = 1.0

        self.previous = position + variable_search.get()

    def search_all(self):
        self.stop_searching = False
        self.widget_text_code.tag_remove("search", "1.0", "end")

        while not self.stop_searching:
            self.search(all_=True,
                        next_=True,
                        match_case=not self.findbar.variable_match_case.get(),
                        exact=not self.findbar.variable_exact.get(),
                        regular_expression=not self.findbar.variable_regular_expression.get())

    def replace(self):
        search = self.widget_text_code.tag_nextrange("search", "1.0", "end")

        try:
            self.widget_text_code.delete(search[0], search[1])
            self.widget_text_code.insert(search[0], self.replacebar.search_entry.entry.get())
        except IndexError:
            self.stop_replacing = True

    def replace_all(self):
        self.stop_replacing = False

        while not self.stop_replacing:
            self.replace()


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(pk.Toolbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Toolbar.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.widget_button_save = self.add_button(text="Save", image=self.parent.image_save, command=self.parent.save)
        self.widget_button_reload = self.add_button(text="Reload", image=self.parent.image_refresh, command=self.parent.reload)

        self.add_separator()

        self.widget_button_cut = self.add_button(text="Cut", image=self.parent.image_cut, command=self.parent.cut)
        self.widget_button_copy = self.add_button(text="Copy", image=self.parent.image_copy, command=self.parent.copy)
        self.widget_button_paste = self.add_button(text="Paste", image=self.parent.image_paste, command=self.parent.paste)
        self.widget_button_delete = self.add_button(text="Delete", image=self.parent.image_delete, command=self.parent.delete)

        self.add_separator()

        self.widget_button_undo = self.add_button(text="Undo", image=self.parent.image_undo, command=self.parent.undo)
        self.widget_button_redo = self.add_button(text="Redo", image=self.parent.image_redo, command=self.parent.redo)

        self.add_separator()

        self.variable_find = tk.BooleanVar(value=False)
        self.widget_button_find = self.add_checkbutton(text="Find", image=self.parent.image_find, variable=self.variable_find, command=self.parent.open_findbar)
        self.variable_replace = tk.BooleanVar(value=False)
        self.widget_button_replace = self.add_checkbutton(text="Replace", image=self.parent.image_replace, variable=self.variable_replace, command=self.parent.open_replacebar)

        self.widget_button_exit = self.add_button(text="Exit", image=self.parent.image_exit, side="right", command=sys.exit)


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Statusbar.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.variable_status = tk.StringVar()
        self.add_variable(variable=self.variable_status)

        self.add_sizegrip()

        self.variable_caret = tk.StringVar()
        self.add_variable(variable=self.variable_caret, side="right")

        self.add_separator(side="right")

        self.toolbar()
        self.findbar()
        self.replacebar()

    def toolbar(self):
        self.bind_widget(self.parent.toolbar.widget_button_save, self.variable_status, "Save the file", "")
        self.bind_widget(self.parent.toolbar.widget_button_reload, self.variable_status, "Reload the file", "")

        self.bind_widget(self.parent.toolbar.widget_button_cut, self.variable_status, "Cut the selected text to the clipboard", "")
        self.bind_widget(self.parent.toolbar.widget_button_copy, self.variable_status, "Copy the selected text to the clipboard", "")
        self.bind_widget(self.parent.toolbar.widget_button_paste, self.variable_status, "Paste the text from the clipboard", "")
        self.bind_widget(self.parent.toolbar.widget_button_delete, self.variable_status, "Delete the selected text", "")

        self.bind_widget(self.parent.toolbar.widget_button_undo, self.variable_status, "Undo the last action", "")
        self.bind_widget(self.parent.toolbar.widget_button_redo, self.variable_status, "Redo the last action", "")

        self.bind_widget(self.parent.toolbar.widget_button_find, self.variable_status, "Open the findbar", "")
        self.bind_widget(self.parent.toolbar.widget_button_replace, self.variable_status, "Open the replacebar", "")

        self.bind_widget(self.parent.toolbar.widget_button_exit, self.variable_status, "Exit the program", "")

    def findbar(self):
        self.bind_widget(self.parent.findbar.search_entry.button_search, self.variable_status, "Find the first instance of the text in the entry", "")
        self.bind_widget(self.parent.findbar.search_entry.button_clear, self.variable_status, "Clear the text in the entry", "")

        self.bind_widget(self.parent.findbar.button_previous, self.variable_status, "Find the previous instance of the text in the entry", "")
        self.bind_widget(self.parent.findbar.button_next, self.variable_status, "Find the next instance of the text in the entry", "")
        self.bind_widget(self.parent.findbar.button_find_all, self.variable_status, "Find all instances of the text in the entry", "")

        self.bind_widget(self.parent.findbar.checkbutton_match_case, self.variable_status, "", "")
        self.bind_widget(self.parent.findbar.checkbutton_exact, self.variable_status, "", "")
        self.bind_widget(self.parent.findbar.checkbutton_regular_expression, self.variable_status, "", "")

    def replacebar(self):
        self.bind_widget(self.parent.replacebar.search_entry.button_clear, self.variable_status, "Clear the text in the entry", "")

        self.bind_widget(self.parent.replacebar.button_replace, self.variable_status, "Replace the selected instance of the text in the first entry with the text in the second entry", "")
        self.bind_widget(self.parent.replacebar.button_replace_all, self.variable_status, "Replace all instances of the text in the first entry with the text in the second entry", "")

    def check_caret(self, event=None):
        index = str(self.parent.widget_text_code.index("insert")).split(".")
        self.variable_caret.set("Line: {}, Column: {}".format(index[0], index[1]))


class Findbar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.is_text_valid = False
        self.search_entry = SearchEntry(self)
        self.search_entry.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.button_previous = ttk.Button(self, text="Previous", image=self.parent.master.image_find_previous, command=lambda: self.parent.master.search(previous=True,
                                                                                                                                                         match_case=not self.variable_match_case.get(),
                                                                                                                                                         exact=not self.variable_exact.get(),
                                                                                                                                                         regular_expression=not self.variable_regular_expression.get()))
        self.button_previous.pack(side="left")

        self.button_next = ttk.Button(self, text="Next", image=self.parent.master.image_find_next, command=lambda: self.parent.master.search(next_=True,
                                                                                                                                             match_case=not self.variable_match_case.get(),
                                                                                                                                             exact=not self.variable_exact.get(),
                                                                                                                                             regular_expression=not self.variable_regular_expression.get()))
        self.button_next.pack(side="left")

        self.button_find_all = ttk.Button(self, text="Find All", command=self.parent.master.search_all)
        self.button_find_all.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.variable_match_case = tk.BooleanVar(value=0)
        self.checkbutton_match_case = ttk.Checkbutton(self, text="Match Case", variable=self.variable_match_case)
        self.checkbutton_match_case.pack(side="left")

        self.variable_exact = tk.BooleanVar(value=0)
        self.checkbutton_exact = ttk.Checkbutton(self, text="Exact", variable=self.variable_exact)
        self.checkbutton_exact.pack(side="left")

        self.variable_regular_expression = tk.BooleanVar(value=0)
        self.checkbutton_regular_expression = ttk.Checkbutton(self, text="Regular Expression", variable=self.variable_regular_expression)
        self.checkbutton_regular_expression.pack(side="left")

        self.search_entry.check_search()


class Replacebar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.is_text_valid = False
        self.search_entry = SearchEntry(self, replace=True)
        self.search_entry.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.button_replace = ttk.Button(self, text="Replace", command=self.parent.master.replace)
        self.button_replace.pack(side="left")

        self.button_replace_all = ttk.Button(self, text="Replace All", command=self.parent.master.replace_all)
        self.button_replace_all.pack(side="left")


class SearchEntry(ttk.Frame):
    def __init__(self, parent, replace=False, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.replace = replace

        self.columnconfigure(1, weight=1)

        if not self.replace:
            self.variable_search = tk.BooleanVar()
            self.variable_search.trace("w", self.check_search)

            self.button_search = ttk.Button(self, text="Search", image=self.parent.parent.master.image_find, command=lambda: self.parent.parent.master.search(match_case=not self.parent.parent.master.findbar.variable_match_case.get(),
                                                                                                                                                              exact=not self.parent.parent.master.findbar.variable_exact.get(),
                                                                                                                                                              regular_expression=not self.parent.parent.master.findbar.variable_regular_expression.get()))
            self.button_search.grid(row=0, column=0)

            self.variable_entry = tk.StringVar()
            self.variable_entry.trace("w", self.check_entry)

        self.entry = ttk.Entry(self, textvariable=self.variable_entry if not self.replace else None)
        self.entry.grid(row=0, column=1)

        self.button_clear = ttk.Button(self, text="Clear", image=self.parent.parent.master.image_exit, command=self.clear)

        if not self.replace:
            self.check_entry()

    def clear(self):
        self.parent.parent.master.widget_text_code.tag_remove("search", "1.0", "end")
        self.entry.delete(0, "end")

    def check_entry(self, *args):
        if self.variable_entry.get() != "":
            self.entry.master.button_search.configure(state="normal")
            self.button_clear.grid(row=0, column=2)

        else:
            self.entry.master.button_search.configure(state="disabled")
            self.button_clear.grid_remove()

    def check_search(self, *args):
        if not self.replace:
            if self.variable_search.get():
                self.parent.button_next.configure(state="normal")
                self.parent.button_previous.configure(state="normal")

            else:
                self.parent.button_next.configure(state="disabled")
                self.parent.button_previous.configure(state="disabled")


def main():
    app = tk.Tk()
    text = TextEditor(app)
    text.load_file("./test_files/text.txt")
    app.mainloop()


if __name__ == "__main__":
    main()
