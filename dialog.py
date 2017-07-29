#!/usr/bin/env python
# -*- coding: utf-8 -*-
""""""

import tkinter as tk
from tkinter import ttk

import pkinter as pk

__title__ = "Dialog"
__author__ = "DeflatedPickle"
__version__ = "1.0.0"


class Dialog(tk.Toplevel):
    def __init__(self, parent, title=None, *args, **kwargs):
        tk.Toplevel.__init__(self, parent)
        self.parent = parent

        self.title(title)

        ttk.Style().configure("White.TFrame", background="white")

        frame = ttk.Frame(self, style="White.TFrame")
        self.initial_focus = self.body(frame)
        frame.pack(fill="both", expand=True)

        ttk.Separator(self).pack(fill="x")

        self.buttonbox()

        pk.center_on_parent(self)
        self.grab_set()

    def body(self, master):
        pass

    def buttonbox(self):
        box = ttk.Frame(self)

        w = ttk.Button(box, text="OK", width=10, command=self.ok, default="active")
        w.pack(side="left", padx=5, pady=5)
        w = ttk.Button(box, text="Cancel", width=10, command=self.cancel)
        w.pack(side="left", padx=5, pady=5)

        self.bind("<Return>", self.ok)
        self.bind("<Escape>", self.cancel)

        box.pack()

    def ok(self, event=None):
        if not self.validate():
            self.initial_focus.focus_set()
            return

        self.withdraw()
        self.update_idletasks()

        try:
            self.apply()
        finally:
            self.cancel()

    def cancel(self, event=None):
        if self.parent is not None:
            self.parent.focus_set()
        self.destroy()

    def validate(self):
        return 1

    def apply(self):
        pass


class AboutWindow(Dialog):
    pass


class ProgressWindow(Dialog):
    def __init__(self, parent, title=None, maximum: int=0, *args, **kwargs):
        self.maximum = maximum

        self.variable_name = None
        self.variable_progress = None
        self.variable_percent = None
        Dialog.__init__(self, parent, title=title, *args, **kwargs)

    def body(self, master):
        ttk.Style().configure("White.TLabel", background="white")

        self.variable_name = tk.StringVar()
        name = ttk.Label(master, textvariable=self.variable_name, style="White.TLabel")
        name.pack(anchor="w", padx=20, pady=[10, 0])

        self.variable_percent = tk.StringVar()
        percent = ttk.Label(master, textvariable=self.variable_percent, style="White.TLabel")
        percent.pack(anchor="w", padx=20, pady=[0, 10])

        self.variable_progress = tk.IntVar()
        progress = ttk.Progressbar(master, variable=self.variable_progress, maximum=self.maximum)
        progress.pack(fill="x", padx=20, pady=[0, 10])

    def buttonbox(self):
        Dialog.buttonbox(self)
        self.pack_slaves()[2].pack_slaves()[0].destroy()

        self.pack_slaves()[2].pack(fill="x")

        self.pack_slaves()[2].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[2].pack_slaves()[0].configure(default="active")

        self.pack_slaves()[1].destroy()

        self.geometry("450x127")
        self.resizable(False, False)


def main():
    app = tk.Tk()
    progress = ProgressWindow(app, maximum=100)
    progress.variable_name.set("Current File: life.txt")
    progress.variable_percent.set("42% Complete")
    progress.variable_progress.set(42)
    app.mainloop()


if __name__ == "__main__":
    main()
