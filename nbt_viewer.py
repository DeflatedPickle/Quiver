#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The NBT-viewer for Quiver."""

import tkinter as tk
from tkinter import ttk

import pkinter as pk
from nbt import nbt

__title__ = "Feather"
__author__ = "DeflatedPickle"
__version__ = "1.1.2"


class NBTViewer(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Feather")
        self.geometry("800x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        # self.transient(parent)
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

        self.scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_tree, orient="horizontal",
                                                  command=self.widget_treeview.xview)
        self.scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.scrollbar_vertical = ttk.Scrollbar(self.widget_frame_tree, orient="vertical",
                                                command=self.widget_treeview.yview)
        self.scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_treeview.configure(xscrollcommand=self.scrollbar_horizontal.set,
                                       yscrollcommand=self.scrollbar_vertical.set)

    def load_nbt(self, file=""):
        self.file = file
        nbt_file = nbt.NBTFile(file, "rb")

        first = self.widget_treeview.insert(parent="",
                                            index="end",
                                            iid=nbt_file.name,
                                            text=nbt_file.name,
                                            tags="Bold")

        self.widget_treeview.item(first, open=True)

        parent = nbt_file.name

        for tag in nbt_file.tags:
            # print(tag.name, tag.valuestr(), tag.__class__.__name__)

            self.widget_treeview.insert(parent=parent,
                                        index="end",
                                        iid=tag.name,
                                        text=tag.name,
                                        values=[tag.valuestr(),
                                                tag.__class__.__name__],
                                        tags="Bold" if tag.__class__.__name__ in ["TAG_Compound", "TAG_List"] else "")

            if tag.__class__.__name__ in ["TAG_Compound", "TAG_List"]:
                parent = tag.name

                for compound_tag in nbt_file[tag.name].tags:
                    self.load_nested(parent, nbt_file, compound_tag, tag)

                parent = nbt_file.name

        self.title("Feather - {}".format(file))

    def load_nested(self, parent, nbt_file, tag, compound_tag=None):
        # print("Name: " + (tag.name if tag.name else ""), "Value: " + tag.valuestr(), "Type: " + tag.__class__.__name__, sep=" | ")

        self.widget_treeview.insert(parent=parent,
                                    index="end",
                                    iid="" if tag.name in ["name", "value"] else tag.name,
                                    text=tag.name if tag.name else "",
                                    values=[tag.valuestr(),
                                            tag.__class__.__name__],
                                    tags="Bold" if tag.__class__.__name__ in ["TAG_Compound", "TAG_List"] else "")

        if tag.__class__.__name__ in ["TAG_Compound", "TAG_List"]:
            try:
                for compound in nbt_file[compound_tag.name][tag.name].tags:
                    self.load_nested(tag.name, nbt_file, compound)
            except TypeError:
                pass


class Tree(ttk.Treeview):
    def __init__(self, parent, window, **kwargs):
        ttk.Treeview.__init__(self, parent, selectmode="browse", columns=["", "", ""], **kwargs)
        self.parent = window

        self.heading("#0", text="Element")
        self.heading("#1", text="Value")
        self.column("#1", width=250)
        self.heading("#2", text="Type")
        self.column("#2", width=120, stretch=False)

        self.tag_configure("Bold", font=("", "10", "bold"))

    def refresh(self):
        self.delete(*self.parent.widget_tree.get_children())
        self.parent.load_nbt(self.parent.file)


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(pk.Toolbar):
    def __init__(self, parent, *args):
        pk.Toolbar.__init__(self, parent, *args)
        self.parent = parent


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args):
        pk.Statusbar.__init__(self, parent, *args)

        self.status_variable = tk.StringVar()
        self.add_variable(variable=self.status_variable)

        self.add_sizegrip()


def main():
    app = tk.Tk()
    nbt_view = NBTViewer(app)
    nbt_view.load_nbt("./test_files/bigtest.nbt")
    app.mainloop()


if __name__ == "__main__":
    main()
