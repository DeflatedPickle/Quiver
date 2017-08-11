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
import tempfile
import sys
import threading

import pkinter as pk

import project_window
import dialog
import functions

__title__ = "StartWindow"
__author__ = "DeflatedPickle"
__version__ = "1.3.0"


class StartWindow(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Quiver")
        self.geometry("200x300")
        self.resizable(width=False, height=False)
        self.transient(parent)
        self.grab_set()
        self.protocol("WM_DELETE_WINDOW", sys.exit)
        self.rowconfigure(0, weight=1)
        self.columnconfigure((0, 1), weight=1)

        self.resourcepack_location = os.getenv("APPDATA").replace("\\", "/") + "/.minecraft/resourcepacks"
        self.resourcepack_location_server = os.getenv("APPDATA").replace("\\",
                                                                         "/") + "/.minecraft/server-resource-packs"

        self.widget_button_new = ttk.Button(self, text="New Pack", command=self.create_new).grid(row=0, column=0,
                                                                                                 columnspan=2,
                                                                                                 sticky="nesw")
        self.widget_button_manage_packs = ttk.Button(self, text="Manage Packs", state="disabled",
                                                     command=lambda: dialog.ManagePacks(self.parent)).grid(
            row=1, column=0,
            columnspan=2,
            sticky="ew")
        self.widget_button_open = ttk.Button(self, text="Open Pack", command=self.open_pack).grid(row=2, rowspan=2,
                                                                                                  column=0,
                                                                                                  sticky="nesw")
        self.widget_button_install = ttk.Button(self, text="Install Pack", command=self.install_pack).grid(row=2,
                                                                                                           column=1,
                                                                                                           sticky="ew")
        self.widget_button_install = ttk.Button(self, text="Install Zip", command=self.install_zip).grid(row=3,
                                                                                                         column=1,
                                                                                                         sticky="ew")
        self.widget_button_open_zip = ttk.Button(self, text="Open Zip",
                                                 command=lambda: threading.Thread(target=self.open_zip).start()).grid(
            row=4, rowspan=2, column=0, sticky="nesw")
        self.widget_button_patch = ttk.Button(self, text="Install Server Pack", command=self.install_server_pack).grid(
            row=4, column=1, sticky="ew")
        self.widget_button_patch = ttk.Button(self, text="Patch Pack", command=self.patch_pack, state="disabled").grid(
            row=5, column=1, sticky="ew")
        self.widget_button_exit = ttk.Button(self, text="Exit", command=sys.exit).grid(row=6, column=0, columnspan=2,
                                                                                       sticky="ew")

    def create_new(self):
        self.destroy()
        pk.center_on_parent(project_window.ProjectWindow(self.parent))

    def open_pack(self):
        pack = filedialog.askdirectory(initialdir=self.resourcepack_location)
        if os.path.isfile(pack + "/pack.mcmeta"):
            # messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
            self.parent.directory = pack
            self.parent.cmd.tree_refresh()
            self.destroy()
        else:
            messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")

    def open_zip(self):
        pack = filedialog.askopenfile("r", initialdir=self.resourcepack_location)
        found_pack = False

        if pack:
            amount = functions.zip_files(pack.name)
            progress = dialog.ProgressWindow(self.parent, title="Opening Zip", maximum=amount)

            count = 0

            with zipfile.ZipFile(pack.name, "r") as z:
                for file in z.namelist():
                    if file == "pack.mcmeta":
                        # messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
                        found_pack = True
                        self.destroy()

                if found_pack:
                    self.parent.d = tempfile.TemporaryDirectory()
                    for file in z.namelist():
                        z.extract(file, self.parent.d.name)

                        count += 1
                        progress.variable_name.set("Current File: " + file)
                        progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
                        progress.variable_progress.set(progress.variable_progress.get() + 1)

                    self.parent.name = pack.name.split("/")[-1].split(".")[0]
                    self.parent.directory = self.parent.d.name
                    self.parent.directory_real = pack.name
                    self.parent.cmd.tree_refresh()
                    self.destroy()

                elif not found_pack:
                    messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")

                pack.close()
            progress.destroy()

    def install_pack(self):
        pack = filedialog.askdirectory()
        if os.path.isfile(pack + "/pack.mcmeta"):
            # messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
            try:
                shutil.move(pack, self.resourcepack_location)
            except shutil.Error:
                messagebox.showerror("Error", "This pack is already installed.")
        else:
            messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")

    def install_zip(self):
        pack = filedialog.askopenfile("r")
        found_pack = False

        if pack:
            with zipfile.ZipFile(pack.name, "r") as z:
                for file in z.namelist():
                    if file == "pack.mcmeta":
                        # messagebox.showinfo("Information", "Found 'pack.mcmeta'.")
                        found_pack = True

                pack.close()

            if found_pack:
                try:
                    shutil.move(pack.name, self.resourcepack_location)
                except shutil.Error:
                    messagebox.showerror("Error", "This pack is already installed.")

            else:
                messagebox.showerror("Error", "Could not find 'pack.mcmeta'.")

    def install_server_pack(self):
        pack = filedialog.askopenfile(initialdir=self.resourcepack_location_server)
        pack.close()
        os.rename(pack.name, pack.name + ".zip")
        shutil.move(pack.name + ".zip", self.resourcepack_location)

    def patch_pack(self):
        pass


def main():
    app = tk.Tk()
    StartWindow(app)
    app.mainloop()


if __name__ == "__main__":
    main()
