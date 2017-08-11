#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The mod detector window for Quiver."""

import tkinter as tk
import _tkinter
from tkinter import ttk
import idlelib.ToolTip
import os
import zipfile
import json

__title__ = "Mod Detector"
__author__ = "DeflatedPickle"
__version__ = "1.7.6"


class ModDetector(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Mod Detector")
        self.geometry("550x270")
        self.resizable(width=False, height=False)
        self.transient(parent)
        self.grab_set()

        # TODO: Re-do this as a dialog using dialog.TreeDialog

        try:
            self.minecraft_location = parent.minecraft_location
            self.minecraft_mods = parent.minecraft_mods

        except AttributeError:
            self.minecraft_location = os.getenv("APPDATA").replace("\\", "/") + "/.minecraft"
            self.minecraft_mods = self.minecraft_location + "/mods"

        self.widget_frame_main = ttk.Frame(self)
        self.widget_frame_main.pack(side="top", fill="both")
        self.widget_frame_main.rowconfigure(0, weight=1)
        self.widget_frame_main.columnconfigure((0, 2), weight=1)

        self.widget_tree_left = Tree(self.widget_frame_main)
        self.widget_tree_left.grid(row=0, column=0, sticky="nesw")

        self.widget_frame_buttons = ttk.Frame(self.widget_frame_main)
        self.widget_frame_buttons.grid(row=0, column=1)

        ttk.Style().configure("All.TButton", font=("", "10", "underline"))

        self.widget_button_right_all = ttk.Button(self.widget_frame_buttons, text=">>>",
                                                  command=self.move_all_mods_right, style="All.TButton")
        self.widget_button_right_all.grid(row=0, column=0, sticky="we")
        idlelib.ToolTip.ToolTip(self.widget_button_right_all, "Add all of the mods")

        self.widget_button_right = ttk.Button(self.widget_frame_buttons, text=">>>", command=self.move_mod_right)
        self.widget_button_right.grid(row=1, column=0, sticky="we")
        idlelib.ToolTip.ToolTip(self.widget_button_right, "Add the selected mod")

        self.widget_button_left = ttk.Button(self.widget_frame_buttons, text="<<<", command=self.move_mod_left)
        self.widget_button_left.grid(row=2, column=0, sticky="we")
        idlelib.ToolTip.ToolTip(self.widget_button_left, "Remove the selected mod")

        self.widget_button_left_all = ttk.Button(self.widget_frame_buttons, text="<<<",
                                                 command=self.move_all_mods_left, style="All.TButton")
        self.widget_button_left_all.grid(row=3, column=0, sticky="we")
        idlelib.ToolTip.ToolTip(self.widget_button_left_all, "Remove all of the mods")

        self.widget_tree_right = Tree(self.widget_frame_main)
        self.widget_tree_right.grid(row=0, column=2, sticky="nesw")

        self.widget_frame_buttons_bottom = ttk.Frame(self)
        self.widget_frame_buttons_bottom.pack(side="bottom", fill="x")

        self.widget_button_cancel = ttk.Button(self.widget_frame_buttons_bottom, text="Cancel",
                                               command=self.exit_mod).pack(side="right")
        self.widget_button_confirm = ttk.Button(self.widget_frame_buttons_bottom, text="OK", command=self.confirm_mods,
                                                default="active")
        self.widget_button_confirm.pack(side="right")

        self.mod_search()

    def mod_search(self):
        # FIXME: Can't find mods in subfolders.
        # Use os.walk() instead.
        for file in os.listdir(self.minecraft_mods):
            if file.endswith(".jar") or file.endswith(".litemod"):
                self.widget_tree_left.widget_tree.insert(parent="",
                                                         index="end",
                                                         text=os.path.splitext(file)[0] if not
                                                         self.load_mcmodinfo(os.path.join(self.minecraft_mods, file))[
                                                             "name"] else
                                                         self.load_mcmodinfo(os.path.join(self.minecraft_mods, file))[
                                                             "name"],
                                                         values=(os.path.splitext(file)[1]),
                                                         tags=(os.path.join(self.minecraft_mods + "/", file), "mod"))

    def load_mcmodinfo(self, file):
        with zipfile.ZipFile(file) as z:
            with z.open("mcmod.info") as info:
                return json.loads(info.read().decode("utf-8"), strict=False)[0]

    def move_mod_right(self):
        try:
            item = self.widget_tree_left.widget_tree.item(self.widget_tree_left.widget_tree.focus())
            self.widget_tree_left.widget_tree.delete(self.widget_tree_left.widget_tree.focus())
            self.widget_tree_right.widget_tree.insert(parent="", index="end", text=item["text"], values=item["values"],
                                                      tags=item["tags"])
        except _tkinter.TclError:
            pass

    def move_mod_left(self):
        try:
            item = self.widget_tree_right.widget_tree.item(self.widget_tree_right.widget_tree.focus())
            self.widget_tree_right.widget_tree.delete(self.widget_tree_right.widget_tree.focus())
            self.widget_tree_left.widget_tree.insert(parent="", index="end", text=item["text"], values=item["values"],
                                                     tags=item["tags"])
        except _tkinter.TclError:
            pass

    def move_all_mods_right(self):
        for item in self.widget_tree_left.widget_tree.get_children():
            item = self.widget_tree_left.widget_tree.item(item)
            self.widget_tree_right.widget_tree.insert(parent="", index="end", text=item["text"], values=item["values"],
                                                      tags=item["tags"])
        self.widget_tree_left.widget_tree.delete(*self.widget_tree_left.widget_tree.get_children())

    def move_all_mods_left(self):
        for item in self.widget_tree_right.widget_tree.get_children():
            item = self.widget_tree_right.widget_tree.item(item)
            self.widget_tree_left.widget_tree.insert(parent="", index="end", text=item["text"], values=item["values"],
                                                     tags=item["tags"])
        self.widget_tree_right.widget_tree.delete(*self.widget_tree_right.widget_tree.get_children())

    def confirm_mods(self):
        items = self.widget_tree_right.widget_tree.get_children()
        actual_items = []
        for i in items:
            actual_items.append(self.widget_tree_right.widget_tree.item(i))
        self.parent.included_mods = actual_items
        self.destroy()

    def exit_mod(self):
        self.destroy()


class Tree(ttk.Frame):
    def __init__(self, parent, **kwargs):
        ttk.Frame.__init__(self, parent, **kwargs)
        self.parent = parent
        self.rowconfigure(0, weight=1)
        self.columnconfigure(0, weight=1)

        self.widget_tree = ttk.Treeview(self, selectmode="browse", columns=[""])
        self.widget_tree.grid(row=0, column=0, sticky="nesw")

        self.widget_tree.configure(selectmode="browse")
        self.widget_tree.heading("#0", text="Mod Name")
        self.widget_tree.heading("#1", text="Mod Extension")
        self.widget_tree.column("#1", width=100, stretch=False)

        self.scrollbar_horizontal = ttk.Scrollbar(self, orient="horizontal", command=self.widget_tree.xview)
        self.scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.scrollbar_vertical = ttk.Scrollbar(self, orient="vertical", command=self.widget_tree.yview)
        self.scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_tree.configure(xscrollcommand=self.scrollbar_horizontal.set,
                                   yscrollcommand=self.scrollbar_vertical.set)


def main():
    app = tk.Tk()
    app.widget_frame_buttons = ttk.Frame(app)
    app.widget_frame_buttons.pack(side="bottom", fill="x")

    ModDetector(app)
    app.mainloop()


if __name__ == "__main__":
    main()
