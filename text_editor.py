#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The text-editor for Quiver."""

import tkinter as tk
from _tkinter import TclError
from tkinter import ttk
import os
import sys

import pkinter as pk

import load_images


class TextEditor(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Redstone")
        image = load_images.LoadImages()
        self.geometry("500x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        self.transient(parent)
        self.rowconfigure(2, weight=1)
        self.columnconfigure(0, weight=1)

        self.image_find = image.image_find

        self.image_replace = image.image_replace

        self.image_cut = image.image_cut
        self.image_copy = image.image_copy
        self.image_paste = image.image_paste
        self.image_delete = image.image_delete

        self.image_undo = image.image_undo
        self.image_redo = image.image_redo

        self.image_exit = image.image_exit

        self.file = None

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.statusbar = Statusbar(self)
        self.statusbar.grid(row=2, column=0, sticky="we")

        self.widget_frame_search = ttk.Frame(self)

        self.findbar = Findbar(self.widget_frame_search)
        self.replacebar = Replacebar(self.widget_frame_search)

        ##################################################

        self.widget_frame_code = ttk.Frame(self)
        self.widget_frame_code.grid(row=2, column=0, sticky="nesw")
        self.widget_frame_code.rowconfigure(0, weight=1)
        self.widget_frame_code.columnconfigure(1, weight=1)

        self.widget_text_code = tk.Text(self.widget_frame_code, wrap="none", undo=True, width=0, height=0)
        self.widget_text_code.grid(row=0, column=1, sticky="nesw")

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
            self.title("{} - {}".format(self.title(), "".join(os.path.splitext(file))))
            f.close()

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
        except TclError:
            pass

    def redo(self):
        try:
            self.widget_text_code.edit_redo()
        except TclError:
            pass

    def find(self):
        if self.toolbar.variable_find.get():
            self.widget_frame_search.grid(row=1, column=0, sticky="ew")
            self.findbar.grid(row=0, column=0, sticky="ew")
            self.findbar.search_entry.entry.focus_set()

        elif self.toolbar.variable_replace.get():
            self.toolbar.variable_find.set(True)

        else:
            self.widget_frame_search.grid_remove()
            self.findbar.grid_remove()

    def replace(self):
        if self.toolbar.variable_replace.get():
            self.toolbar.variable_find.set(True)
            self.find()
            self.replacebar.grid(row=1, column=0, sticky="ew")

        else:
            self.find()
            self.replacebar.grid_remove()


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(pk.Toolbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Toolbar.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.widget_button_save = self.add_button(text="Save")
        self.widget_button_reload = self.add_button(text="Reload")

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
        self.widget_button_find = self.add_checkbutton(text="Find", image=self.parent.image_find, variable=self.variable_find, command=self.parent.find)
        self.variable_replace = tk.BooleanVar(value=False)
        self.widget_button_replace = self.add_checkbutton(text="Replace", image=self.parent.image_replace, variable=self.variable_replace, command=self.parent.replace)

        self.widget_button_exit = self.add_button(text="Exit", image=self.parent.image_exit, side="right", command=sys.exit)


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Statusbar.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.status_variable = tk.StringVar()
        self.add_variable(textvariable=self.status_variable)

        self.bind_widget(parent.toolbar.widget_button_save, self.status_variable, "Save the file", "")
        self.bind_widget(parent.toolbar.widget_button_reload, self.status_variable, "Reload the file", "")

        self.bind_widget(parent.toolbar.widget_button_cut, self.status_variable,
                         "Cut the selected text to the clipboard", "")
        self.bind_widget(parent.toolbar.widget_button_copy, self.status_variable,
                         "Copy the selected text to the clipboard", "")
        self.bind_widget(parent.toolbar.widget_button_paste, self.status_variable,
                         "Paste the text from the clipboard", "")

        self.bind_widget(parent.toolbar.widget_button_undo, self.status_variable, "Undo the last action", "")
        self.bind_widget(parent.toolbar.widget_button_redo, self.status_variable, "Redo the last action", "")

        self.bind_widget(parent.toolbar.widget_button_find, self.status_variable, "Find a piece of text", "")
        self.bind_widget(parent.toolbar.widget_button_replace, self.status_variable, "Replace a piece of text", "")

        self.bind_widget(parent.toolbar.widget_button_exit, self.status_variable, "Exit the program", "")

        self.add_sizegrip()


class Findbar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.search_entry = SearchEntry(self)
        self.search_entry.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.button_previous = ttk.Button(self, text="Previous")
        self.button_previous.pack(side="left")

        self.button_next = ttk.Button(self, text="Next")
        self.button_next.pack(side="left")

        self.button_find_all = ttk.Button(self, text="Find All")
        self.button_find_all.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.variable_match_case = tk.BooleanVar(value=0)
        self.checkbutton_match_case = ttk.Checkbutton(self, text="Match Case", variable=self.variable_match_case)
        self.checkbutton_match_case.pack(side="left")


class Replacebar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.search_entry = SearchEntry(self)
        self.search_entry.pack(side="left")

        ttk.Separator(self, orient="vertical").pack(side="left", fill="y", padx=3, pady=1)

        self.button_replace = ttk.Button(self, text="Replace")
        self.button_replace.pack(side="left")

        self.button_replace_all = ttk.Button(self, text="Replace All")
        self.button_replace_all.pack(side="left")


class SearchEntry(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.columnconfigure(1, weight=1)

        self.button_search = ttk.Button(self, text="Search", image=self.parent.parent.master.image_find)
        self.button_search.grid(row=0, column=0)

        self.entry = ttk.Entry(self)
        self.entry.grid(row=0, column=1)

        self.button_clear = ttk.Button(self, text="Clear", image=self.parent.parent.master.image_exit, command=self.clear)
        self.button_clear.grid(row=0, column=2)

    def clear(self):
        self.entry.delete(0, "end")


def main():
    app = tk.Tk()
    text = TextEditor(app)
    text.load_file("./test_files/text.txt")
    app.mainloop()


if __name__ == "__main__":
    main()
