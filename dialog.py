#!/usr/bin/env python
# -*- coding: utf-8 -*-
""""""

import tkinter as tk
from tkinter import font
from tkinter import ttk

import pkinter as pk

__title__ = "Dialog"
__author__ = "DeflatedPickle"
__version__ = "1.7.0"


class Dialog(tk.Toplevel):
    def __init__(self, parent, title="Dialog", transient=True, resizable=(False, False), geometry="300x300", separator=False, background="white", *args,
                 **kwargs):
        tk.Toplevel.__init__(self, parent)
        self.parent = parent

        self.title(title)

        ttk.Style().configure("White.TFrame", background=background)

        frame = ttk.Frame(self, style="White.TFrame")
        self.initial_focus = self.body(frame)
        frame.pack(fill="both", expand=True)

        if separator:
            ttk.Separator(self).pack(fill="x")

        self.buttonbox()

        self.update()

        if transient:
            self.transient(self.parent)

        if geometry:
            self.geometry(geometry)

        if resizable:
            self.resizable(resizable[0], resizable[1])

        pk.center_on_parent(self)
        self.grab_set()

    def body(self, master):
        pass

    def buttonbox(self):
        box = ttk.Frame(self)

        ok = ttk.Button(box, text="OK", width=10, command=self.ok, default="active")
        ok.pack(side="left", padx=5, pady=5)

        cancel = ttk.Button(box, text="Cancel", width=10, command=self.cancel)
        cancel.pack(side="left", padx=5, pady=5)

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

        del event

    def cancel(self, event=None):
        if self.parent is not None:
            self.parent.focus_set()
        self.destroy()

        del event

    def validate(self):
        return True

    def apply(self):
        pass


class TreeDialog(Dialog):
    def __init__(self, parent, title="Tree Dialog", separator=False, columns=[], headings={}, *args, **kwargs):
        self.columns = columns
        self.headings = headings
        Dialog.__init__(self, parent, title=title, transient=True, geometry="550x300", separator=separator, *args, **kwargs)

    def body(self, master):
        self.tree = ttk.Treeview(master, columns=self.columns)
        self.tree.pack(side="left", fill="both", expand=True)

        for item in self.headings:
            self.tree.heading(item, text=self.headings[item].get("text", ""))
            self.tree.column(item, width=self.headings[item].get("width", 100), stretch=self.headings[item].get("stretch", True))


class CreditWindow(Dialog):
    def __init__(self, parent, title="Credits", *args, **kwargs):
        Dialog.__init__(self, parent, title=title, transient=True, geometry="300x200", *args, **kwargs)

    def body(self, master):
        text = tk.Text(master, relief="flat", wrap="word", width=0, height=0)
        text.pack(side="left", fill="both", expand=True)
        text.tag_configure("header", justify="center",
                           font=(font.nametofont("TkDefaultFont").cget("family"), 12, "bold"))
        text.tag_configure("hyperlink")

        scrollbar = ttk.Scrollbar(master, orient="vertical", command=text.yview)
        scrollbar.pack(side="right", fill="y")

        text.insert("end", "Libraries:\n", "header")
        text.insert("end", """- Fredrik Lundh for Tkinter.
- Alex Clark for Pillow.
- okdana for jsonesque.
- twoolie for NBT.\n\n""")
        text.insert("end", "Programs:\n", "header")
        text.insert("end", """- MightyPork for ResourcePack Workbench.
- dotPDN, LLC for Paint.NET.
- JetBrains for PyCharm.
- Mojang for Minecraft.\n\n""")
        text.insert("end", "Other:\n", "header")
        text.insert("end", """- Hartmut Goebel, Martin Zibricky, David Cortesi and David Vierra for PyInstaller.
- GitHub for hosting the code.
- Bryan Oakley on StackOverflow for occasional indirect help with code.""")

        text.configure(state="disabled", yscrollcommand=scrollbar.set)

    def buttonbox(self):
        Dialog.buttonbox(self)

        self.pack_slaves()[1].pack_slaves()[0].destroy()

        self.pack_slaves()[1].pack(fill="x")

        self.pack_slaves()[1].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[1].pack_slaves()[0].configure(text="Close", default="active")


class LicenceWindow(Dialog):
    def __init__(self, parent, title="Licence", *args, **kwargs):
        Dialog.__init__(self, parent, title=title, transient=True, geometry="550x300", *args, **kwargs)

    def body(self, master):
        text = tk.Text(master, relief="flat", wrap="word", width=0, height=0)
        text.pack(side="left", fill="both", expand=True)

        scrollbar = ttk.Scrollbar(master, orient="vertical", command=text.yview)
        scrollbar.pack(side="right", fill="y")

        with open("./LICENSE") as file:
            text.insert(1.0, file.read())

        text.configure(state="disabled", yscrollcommand=scrollbar.set)

    def buttonbox(self):
        Dialog.buttonbox(self)

        self.pack_slaves()[1].pack_slaves()[0].destroy()

        self.pack_slaves()[1].pack(fill="x")

        self.pack_slaves()[1].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[1].pack_slaves()[0].configure(text="Close", default="active")


class AboutWindow(Dialog):
    def __init__(self, parent, title="Program", author="DeflatedPickle", version="0.0.0", logo=None,
                 description="A basic description.", copyright_text="Copyright © 2017\nDeflatedPickle", *args,
                 **kwargs):
        self.program_title = title
        self.author = author
        self.version = version
        self.logo = logo
        self.description = description
        self.copyright_text = copyright_text
        self.credit = None

        Dialog.__init__(self, parent, title=("About", title), transient=True, geometry="250x250", *args, **kwargs)

    def body(self, master):
        ttk.Style().configure("White.TLabel", background="white")

        ttk.Label(master, image=self.logo, justify="center", style="White.TLabel").pack(pady=10)

        title = font.Font(family=font.nametofont("TkDefaultFont").cget("family"), size=15, weight="bold")
        ttk.Label(master, text=(self.program_title, self.version), font=title, justify="center",
                  style="White.TLabel").pack()

        ttk.Label(master, text=self.description, wraplength=230, justify="center", style="White.TLabel").pack()
        ttk.Label(master, text=self.copyright_text, justify="center", style="White.TLabel").pack()

        link = pk.Hyperlink(master, text="Visit the project on GitHub", link="https://github.com/DeflatedPickle/Quiver")
        link.configure(background="white", justify="center")
        link.pack(pady=3)
        link._font.configure(size=10)

    def buttonbox(self):
        Dialog.buttonbox(self)

        self.pack_slaves()[1].pack_slaves()[0].destroy()

        self.pack_slaves()[1].pack(fill="x")

        self.pack_slaves()[1].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[1].pack_slaves()[0].configure(text="Close", default="active")

        ttk.Button(self.pack_slaves()[1], text="Credits", command=lambda: CreditWindow(self)).pack(side="left", padx=5,
                                                                                                   pady=5)
        ttk.Button(self.pack_slaves()[1], text="Licence", command=lambda: LicenceWindow(self)).pack(side="left", padx=5,
                                                                                                    pady=5)


class ProgressWindow(Dialog):
    def __init__(self, parent, title="Progress", maximum: int = 0, *args, **kwargs):
        self.maximum = maximum

        self.variable_name = None
        self.variable_progress = None
        self.variable_percent = None

        Dialog.__init__(self, parent, title=title, transient=False, geometry="450x127", *args, **kwargs)

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

        self.pack_slaves()[1].pack_slaves()[0].destroy()

        self.pack_slaves()[1].pack(fill="x")

        self.pack_slaves()[1].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[1].pack_slaves()[0].configure(default="active")


class ManagePacks(TreeDialog):
    def __init__(self, parent, title="Manage Packs", *args, **kwargs):
        TreeDialog.__init__(self, parent, title=title, background=ttk.Style().lookup("TFrame", "background"), separator=True, *args, **kwargs)

    def body(self, master):
        TreeDialog.body(self, master)

        frame = ttk.Frame(master)
        frame.pack(side="right")

        frame_one = ttk.Frame(frame)
        frame_one.pack(pady=5)

        add = ttk.Button(frame_one, text="Add Pack")
        add.pack()

        remove = ttk.Button(frame_one, text="Remove Pack")
        remove.pack()

        frame_two = ttk.Frame(frame)
        frame_two.pack(pady=5)

        enable = ttk.Button(frame_two, text="Enable Pack")
        enable.pack()

        disable = ttk.Button(frame_two, text="Disable Pack")
        disable.pack()

    def buttonbox(self):
        Dialog.buttonbox(self)

        self.pack_slaves()[2].pack(fill="x")

        self.pack_slaves()[2].pack_slaves()[0].pack(side="right")
        self.pack_slaves()[2].pack_slaves()[0].configure(default="active")

        self.pack_slaves()[2].pack_slaves()[1].pack(side="right")

        apply = ttk.Button(self.pack_slaves()[2], text="Apply", command=self.apply)
        apply.pack(side="right", padx=5, pady=5)


def main():
    app = tk.Tk()
    pk.center_on_screen(app)

    # Dialog(app)
    # TreeDialog(app, columns=[""], headings={"#0": {"text": "1"}, "#1": {"text": "1", "width": 50, "stretch": False}})

    # AboutWindow(app)

    # progress = ProgressWindow(app, maximum=100)
    # progress.variable_name.set("Current File: life.txt")
    # progress.variable_percent.set("42% Complete")
    # progress.variable_progress.set(42)

    ManagePacks(app)

    app.mainloop()


if __name__ == "__main__":
    main()
