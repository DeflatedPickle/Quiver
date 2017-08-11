#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The project window for Quiver."""

import tkinter as tk
from tkinter import messagebox
from tkinter import ttk
import os
import json
import zipfile  # https://docs.python.org/3.4/library/zipfile.html
from datetime import datetime
import threading
import sys

import pkinter as pk

import mod_detector
import dialog
import functions

__title__ = "ProjectWindow"
__author__ = "DeflatedPickle"
__version__ = "1.4.0"


class ProjectWindow(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Project Window")
        self.geometry("300x250")
        self.minsize(width=300, height=150)
        self.maxsize(width=500, height=300)
        self.transient(parent)
        self.grab_set()
        self.protocol("WM_DELETE_WINDOW", sys.exit)

        # TODO: Re-do this as a dialog using dialog.Dialog

        if self.parent.operating_system == "Windows":
            self.minecraft_location = os.getenv("APPDATA").replace("\\", "/") + "/.minecraft"
        elif self.parent.operating_system == "Darwin":
            self.minecraft_location = os.path.expanduser(os.path.join("~", "Library/Application Support/minecraft"))
        self.minecraft_versions = self.minecraft_location + "/versions"
        self.minecraft_resource_packs = self.minecraft_location + "/resourcepacks"
        self.minecraft_mods = self.minecraft_location + "/mods"

        self.included_mods = ""
        self.pack_location = None

        self.widget_frame_body = ttk.Frame(self)
        self.widget_frame_body.pack(side="top", fill="both", expand=True)
        self.widget_frame_body.rowconfigure(4, weight=1)
        self.widget_frame_body.columnconfigure(1, weight=1)

        ttk.Label(self.widget_frame_body, text="Project Title:").grid(row=0, column=0, sticky="w")
        self.variable_string_title = tk.StringVar()
        self.variable_string_title.set("Minecraft Resource Pack")
        self.widget_entry_title = ttk.Entry(self.widget_frame_body,
                                            textvariable=self.variable_string_title)
        self.widget_entry_title.grid(row=0, column=1, sticky="we")

        ttk.Label(self.widget_frame_body, text="Project Name:").grid(row=1, column=0, sticky="w")
        self.variable_string_name = tk.StringVar()
        self.variable_string_name.set("Minecraft_Resource_Pack")
        self.widget_entry_name = ttk.Entry(self.widget_frame_body,
                                           textvariable=self.variable_string_name)
        self.widget_entry_name.grid(row=1, column=1, sticky="we")

        ttk.Label(self.widget_frame_body, text="Project Location:").grid(row=2, column=0, sticky="w")
        self.widget_directory_location = pk.DirectoryPicker(self.widget_frame_body)
        self.widget_directory_location.grid(row=2, column=1, sticky="we")
        self.widget_directory_location._variable.set(self.minecraft_resource_packs)

        ttk.Label(self.widget_frame_body, text="Minecraft Version:").grid(row=3, column=0, sticky="w")
        self.widget_combobox_version = ttk.Combobox(self.widget_frame_body, state="readonly")
        self.widget_combobox_version.grid(row=3, column=1, sticky="we")

        ttk.Label(self.widget_frame_body, text="Project Description:").grid(row=4, column=0, sticky="nw")
        self.widget_text_description = tk.Text(self.widget_frame_body, undo=True, width=0, height=0)
        self.widget_text_description.grid(row=4, column=1, sticky="nesw")
        self.widget_text_description.insert(1.0, "The Default Minecraft Resource Pack.")

        ttk.Style().configure("Error.TLabel", foreground="red")
        self.widget_label_error = ttk.Label(self.widget_frame_body, text="Minecraft is not installed.",
                                            style="Error.TLabel")
        # self.widget_label_error.grid(row=5, column=0, columnspan=2)

        self.widget_frame_buttons = ttk.Frame(self)
        self.widget_frame_buttons.pack(side="bottom", fill="x")

        self.widget_button_cancel = ttk.Button(self.widget_frame_buttons, text="Cancel",
                                               command=sys.exit).pack(side="right")
        self.widget_button_create = ttk.Button(self.widget_frame_buttons, text="Create",
                                               command=lambda: threading.Thread(
                                                   target=self.extract_minecraft_jar).start())
        self.widget_button_create.pack(side="right")

        self.widget_button_detect_mods = ttk.Button(self.widget_frame_buttons, text="Detect Mods",
                                                    command=lambda: pk.center_on_parent(mod_detector.ModDetector(self)))
        self.widget_button_detect_mods.pack(side="left")

        try:
            if not os.listdir(self.minecraft_mods):
                self.widget_button_detect_mods.configure(state="disabled")
        except FileNotFoundError:
            self.widget_button_detect_mods.configure(state="disabled")

        self.widget_combobox_version.configure(values=self.find_minecraft_versions())

        try:
            self.widget_combobox_version.set(self.widget_combobox_version["values"][0])
        except IndexError:
            pass
            # self.find_minecraft_versions()

    def find_minecraft_versions(self):
        list_versions = []
        try:
            for file in os.listdir(self.minecraft_versions):
                if os.path.isdir(self.minecraft_versions + "/" + file):
                    if "forge" not in file.lower() and "liteloader" not in file.lower():
                        list_versions.append(file)

        except FileNotFoundError:
            self.widget_label_error.grid(row=5, column=0, columnspan=2)
            self.widget_entry_title.configure(state="disabled")
            self.widget_entry_name.configure(state="disabled")
            self.widget_combobox_version.configure(state="disabled")
            self.widget_directory_location._entry.configure(state="disabled")
            self.widget_directory_location._button.configure(state="disabled")
            self.widget_text_description.configure(state="disabled")
            self.widget_button_create.configure(state="disabled")

        return list_versions

    def extract_minecraft_jar(self):
        minecraft_version = self.widget_combobox_version.get()
        minecraft_jar_path = self.minecraft_versions + "/" + minecraft_version + "/" + minecraft_version + ".jar"
        self.pack_location = self.minecraft_resource_packs + "/" + self.variable_string_name.get()

        amount = functions.zip_files(minecraft_jar_path)
        for item in self.included_mods:
            amount += functions.zip_files(item)

        progress = dialog.ProgressWindow(self.parent, title="Extracting Pack", maximum=amount)

        count = 0

        if os.path.isdir(self.pack_location):
            messagebox.showwarning("Warning", "The pack '{}' already exists.".format(self.variable_string_name.get()))
            delete = messagebox.askyesnocancel("Delete Pack", "Would you like to delete the pack?")
            if delete:
                threading.Thread(target=self.remove_previous_pack).start()
            elif not delete:
                return
        elif not os.path.exists(self.pack_location):
            os.makedirs(self.pack_location)

        with zipfile.ZipFile(minecraft_jar_path, "r") as z:
            # z.extractall(self.widget_directory_location.get() + "/" + self.variable_string_name.get())
            for file in z.namelist():
                if file.startswith("assets/") or file == "pack.png":
                    # print("{} | Extracting: {}".format(datetime.now().strftime("%H:%M:%S"), file))
                    z.extract(file, self.pack_location)

                    count += 1
                    progress.variable_name.set("Current File: " + file)
                    progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
                    progress.variable_progress.set(progress.variable_progress.get() + 1)

        with open(self.pack_location + "/" + "pack.mcmeta", "w+") as file:
            file.write(json.dumps(
                {
                    "pack": {
                        "pack_format": 2,
                        "description": self.widget_text_description.get(1.0, "end").strip("\n") + " - Made with Quiver."
                    }
                }, sort_keys=False, indent=2))

            count += 1
            progress.variable_name.set("Current File: " + file.name)
            progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
            progress.variable_progress.set(progress.variable_progress.get() + 1)

        for item in self.included_mods:
            # print(item)
            with zipfile.ZipFile(" ".join(item["tags"])) as z:
                for file in z.namelist():
                    if file.startswith("assets/"):
                        print("{} | Extracting: {}".format(datetime.now().strftime("%H:%M:%S"), file))
                        z.extract(file, self.pack_location)

                        count += 1
                        progress.variable_name.set("Current File: " + file)
                        progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
                        progress.variable_progress.set(progress.variable_progress.get() + 1)

        progress.destroy()
        messagebox.showinfo(title="Information", message="Extracting complete.")

        self.parent.directory = self.pack_location
        self.parent.cmd.tree_refresh()
        self.destroy()

    def remove_previous_pack(self):
        amount = functions.folder_files(self.pack_location)
        progress = dialog.ProgressWindow(self.parent, title="Deleting Pack", maximum=amount)

        count = 0

        for root, dirs, files in os.walk(self.pack_location, topdown=False):
            # FIXME: Doesn't clear out all files in a directory before trying to delete it.
            for name in files:
                os.remove(os.path.join(root, name))
                # print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), name))

                count += 1
                progress.variable_name.set("Current Folder: " + name)
                progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
                progress.variable_progress.set(progress.variable_progress.get() + 1)

            for name in dirs:
                os.rmdir(os.path.join(root, name))
                # print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), name))

                count += 1
                progress.variable_name.set("Current File: " + name)
                progress.variable_percent.set("{}% Complete".format(round(100 * float(count) / float(amount))))
                progress.variable_progress.set(progress.variable_progress.get() + 1)

        os.rmdir(self.pack_location)
        # print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), self.pack_location))

        progress.destroy()
        messagebox.showinfo(title="Information", message="Deleting complete.")


def main():
    app = tk.Tk()
    app.operating_system = "Windows"

    ProjectWindow(app)
    app.mainloop()


if __name__ == "__main__":
    main()
