#!/usr/bin/env python
"""The text-editor for Quiver."""

import tkinter as tk
from tkinter import ttk
import os

import pkinter as pk

import load_images


class TextEditor(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Redstone")
        self.geometry("500x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        self.transient(parent)
        self.rowconfigure(1, weight=1)
        self.columnconfigure(0, weight=1)

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.statusbar = Statusbar(self)
        self.statusbar.grid(row=2, column=0, sticky="we")

        ##################################################

        self.widget_frame_code = ttk.Frame(self)
        self.widget_frame_code.grid(row=1, column=0, sticky="nesw")
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
        with open(file, "r") as f:
            self.widget_text_code.delete(1.0, "end")
            self.widget_text_code.insert(1.0, f.read())
            self.title("{} - {}".format(self.title(), "".join(os.path.splitext(file))))
            f.close()


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent

        self.widget_button_save = ttk.Button(self, text="Save", style="Toolbutton", state="disabled")
        self.widget_button_save.grid(row=0, column=0)

        self.widget_button_reload = ttk.Button(self, text="Reload", style="Toolbutton", state="disabled")
        self.widget_button_reload.grid(row=0, column=1)

        ttk.Separator(self, orient="vertical").grid(row=0, column=2, sticky="ns")

        self.widget_button_undo = ttk.Button(self, text="Undo", style="Toolbutton", state="disabled")
        self.widget_button_undo.grid(row=0, column=3)

        self.widget_button_redo = ttk.Button(self, text="Redo", style="Toolbutton", state="disabled")
        self.widget_button_redo.grid(row=0, column=4)

        ttk.Separator(self, orient="vertical").grid(row=0, column=5, sticky="ns")

        self.widget_button_cut = ttk.Button(self, text="Cut", style="Toolbutton", state="disabled")
        self.widget_button_cut.grid(row=0, column=6)

        self.widget_button_copy = ttk.Button(self, text="Copy", style="Toolbutton", state="disabled")
        self.widget_button_copy.grid(row=0, column=7)

        self.widget_button_paste = ttk.Button(self, text="Paste", style="Toolbutton", state="disabled")
        self.widget_button_paste.grid(row=0, column=8)

        ttk.Separator(self, orient="vertical").grid(row=0, column=9, sticky="ns")

class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)

        self.status_variable = tk.StringVar()
        self.add_variable(textvariable=self.status_variable)

        self.bind_widget(parent.toolbar.widget_button_save, self.status_variable, "Save the file", "")
        self.bind_widget(parent.toolbar.widget_button_reload, self.status_variable, "Reload the file", "")

        self.bind_widget(parent.toolbar.widget_button_undo, self.status_variable, "Undo the last action", "")
        self.bind_widget(parent.toolbar.widget_button_redo, self.status_variable, "Redo the last action", "")

        self.bind_widget(parent.toolbar.widget_button_cut, self.status_variable,
                         "Cut the selected text to the clipboard", "")
        self.bind_widget(parent.toolbar.widget_button_copy, self.status_variable,
                         "Copy the selected text to the clipboard", "")
        self.bind_widget(parent.toolbar.widget_button_paste, self.status_variable,
                         "Paste the text from the clipboard", "")

        self.add_sizegrip()


def main():
    app = tk.Tk()
    TextEditor(app)
    app.mainloop()


if __name__ == "__main__":
    main()
