#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The NBT-viewer for Quiver."""

import tkinter as tk
from tkinter import ttk

import pkinter as pk
from nbt import nbt


class NBTViewer(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Feather")
        self.geometry("800x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        self.transient(parent)
        self.rowconfigure(1, weight=1)
        self.columnconfigure(0, weight=1)

        self.file = None

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.statusbar = Statusbar(self)
        self.statusbar.grid(row=2, column=0, sticky="we")

        self.widget_paned_window = ttk.PanedWindow(self, orient="horizontal")
        self.widget_paned_window.grid(row=1, column=0, sticky="nesw")

        self.widget_frame_tree = ttk.Frame(self.widget_paned_window)
        self.widget_frame_tree.rowconfigure(0, weight=1)
        self.widget_frame_tree.columnconfigure(0, weight=1)
        self.widget_paned_window.add(self.widget_frame_tree, weight=1)

        self.widget_treeview = Tree(self.widget_frame_tree, self)
        self.widget_treeview.grid(row=0, column=0, sticky="nesw")
        self.widget_treeview.focus_set()

        self.scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_tree,
                                                  orient="horizontal",
                                                  command=self.widget_treeview.xview)
        self.scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.scrollbar_vertical = ttk.Scrollbar(self.widget_frame_tree,
                                                orient="vertical",
                                                command=self.widget_treeview.yview)
        self.scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_treeview.configure(xscrollcommand=self.scrollbar_horizontal.set,
                                       yscrollcommand=self.scrollbar_vertical.set)

    def load_nbt(self, file=""):
        self.file = file
        nbt_file = nbt.NBTFile(file, "rb")

        parent = ""

        for tag in nbt_file.tags:
            # print(tag.name, tag.valuestr(), tag.__class__.__name__)
            self.widget_treeview.insert(parent=parent,
                                        index=0,
                                        iid=tag.name,
                                        text="",
                                        values=["",
                                                tag.name,
                                                tag.valuestr(),
                                                tag.__class__.__name__])

            if tag.__class__.__name__ == "TAG_Compound" or tag.__class__.__name__ == "TAG_List":
                parent = tag.name
                for compound_tag in nbt_file[tag.name].tags:
                    self.widget_treeview.insert(parent=parent,
                                                index=0,
                                                iid=compound_tag.name,
                                                text="",
                                                values=["",
                                                        compound_tag.name,
                                                        compound_tag.valuestr(),
                                                        compound_tag.__class__.__name__])
                parent = ""

        self.title("Feather - {}".format(nbt_file.name))


class Tree(ttk.Treeview):
    def __init__(self, parent, window, *args, **kwargs):
        ttk.Treeview.__init__(self, parent, selectmode="browse", columns=["", "", "", ""], *args, **kwargs)
        self.parent = window

        self.heading("#0", text="Position")
        self.column("#0", width=100, stretch=False)
        self.heading("#1", text="Offset")
        self.column("#1", width=100, stretch=False)
        self.heading("#2", text="Element")
        self.heading("#3", text="Value")
        self.heading("#4", text="Type")
        self.column("#4", width=100, stretch=False)

    def refresh(self):
        self.delete(*self.parent.widget_tree.get_children())
        self.parent.load_nbt(self.parent.file)


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(pk.Toolbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Toolbar.__init__(self, parent, *args, **kwargs)
        self.parent = parent


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Statusbar.__init__(self, parent, *args, **kwargs)

        self.status_variable = tk.StringVar()
        self.add_variable(variable=self.status_variable)

        self.add_sizegrip()


def main():
    app = tk.Tk()
    nbt = NBTViewer(app)
    nbt.load_nbt("./test_files/bigtest.nbt")
    app.mainloop()


if __name__ == "__main__":
    main()
