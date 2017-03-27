#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The start window for Quiver."""

import tkinter as tk
from tkinter import ttk
from tkinter import filedialog
from tkinter import messagebox
import zipfile
import os
import shutil

import project_window


class StartWindow(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Quiver")
        self.geometry("200x300")
        self.resizable(width=False, height=False)
        self.transient(parent)
        self.grab_set()
        self.protocol("WM_DELETE_WINDOW", self.exit_program)
        self.rowconfigure(0, weight=1)
        self.columnconfigure((0, 1), weight=1)

        self.widget_button_new = ttk.Button(self, text="New Pack", command=self.create_new).grid(row=0, column=0, columnspan=2,
                                                                             sticky="nesw")
        self.widget_button_open = ttk.Button(self, text="Open Pack", command=self.open_pack).grid(row=1, rowspan=2, column=0, sticky="nesw")
        self.widget_button_install = ttk.Button(self, text="Install Pack", command=self.install_pack).grid(row=1, column=1, sticky="ew")
        self.widget_button_patch = ttk.Button(self, text="Patch Pack", command=self.patch_pack, state="disabled").grid(row=2, column=1, sticky="ew")
        self.widget_button_exit = ttk.Button(self, text="Exit", command=self.exit_program).grid(row=3, column=0, columnspan=2, sticky="ew")

    def create_new(self):
        project_window.ProjectWindow(self.parent)
        self.destroy()

    def open_pack(self):
        # pack = filedialog.askopenfile("r")
        # pack.close()
        # found_pack = False
        # with zipfile.ZipFile(pack.name, "r") as z:
        #     for file in z.namelist():
        #         if file == "pack.mcmeta":
        #             messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
        #             found_pack = True
        #             self.parent.directory = pack.name
        #             self.parent.cmd.tree_refresh()
        #             self.destroy()
        #     if not found_pack:
        #         messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")

        pack = filedialog.askdirectory(initialdir=os.getenv("APPDATA").replace("\\", "/") + "/.minecraft/resourcepacks")
        if os.path.isfile(pack + "/pack.mcmeta"):
            # messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
            self.parent.directory = pack
            self.parent.cmd.tree_refresh()
            self.destroy()
        else:
            messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")


    def install_pack(self):
        pack = filedialog.askopenfile("r")
        pack.close()

        shutil.move(pack.name, os.getenv("APPDATA").replace("\\", "/") + "/.minecraft/resourcepacks/")

    def patch_pack(self):
        pass

    def exit_program(self):
        raise SystemExit


def main():
    app = tk.Tk()
    StartWindow(app)
    app.mainloop()


if __name__ == "__main__":
    main()
