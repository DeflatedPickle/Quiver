import tkinter as tk
from tkinter import ttk
import os
import json
import zipfile  # https://docs.python.org/3.4/library/zipfile.html
from datetime import datetime

import pkinter as pk


class ProjectWindow(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Project Window")
        self.geometry("300x250")
        self.minsize(width=300, height=150)
        self.transient(parent)
        self.grab_set()
        self.protocol("WM_DELETE_WINDOW", self.exit_program)

        self.minecraft_location = os.getenv("APPDATA") + "\\.minecraft"
        self.minecraft_versions = self.minecraft_location + "\\versions"
        self.minecraft_resource_packs = self.minecraft_location + "\\resourcepacks"

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
        self.widget_directory_location.variable.set(self.minecraft_resource_packs)

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
                                               command=self.exit_program).pack(side="right")
        self.widget_button_create = ttk.Button(self.widget_frame_buttons, text="Create",
                                               command=self.extract_minecraft_jar)
        self.widget_button_create.pack(side="right")

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
                if os.path.isdir(self.minecraft_versions + "\\" + file):
                    if not "forge" in file.lower() and not "liteloader" in file.lower():
                        list_versions.append(file)
        except:
            self.widget_label_error.grid(row=5, column=0, columnspan=2)
            self.widget_entry_title.configure(state="disabled")
            self.widget_entry_name.configure(state="disabled")
            self.widget_combobox_version.configure(state="disabled")
            self.widget_directory_location.entry.configure(state="disabled")
            self.widget_directory_location.button.configure(state="disabled")
            self.widget_text_description.configure(state="disabled")
            self.widget_button_create.configure(state="disabled")

        return list_versions

    def extract_minecraft_jar(self):
        minecraft_version = self.widget_combobox_version.get()
        minecraft_jar_path = self.minecraft_versions + "\\" + minecraft_version + "\\" + minecraft_version + ".jar"
        pack_location = self.minecraft_resource_packs + "\\" + self.variable_string_name.get()

        if not os.path.exists(pack_location):
            os.makedirs(pack_location)

        with zipfile.ZipFile(minecraft_jar_path, "r") as z:
            # z.extractall(self.widget_directory_location.get() + "\\" + self.variable_string_name.get())
            for file in z.namelist():
                if file.startswith("assets/") or file == "pack.png":
                    print("{} | Extracting: {}".format(datetime.now().strftime("%H:%M:%S"), file))
                    z.extract(file, pack_location)

        with open(pack_location + "\\" + "pack.mcmeta", "w+") as file:
            file.write(json.dumps(
            {
                "pack": {
                    "pack_format": 2,
                    "description": self.widget_text_description.get(1.0, "end").strip("\n") + " - Made with Quiver."
                }
            }, sort_keys=False, indent=2))

        self.parent.directory = pack_location
        self.parent.cmd.tree_refresh()
        self.destroy()

    def exit_program(self):
        raise SystemExit


def main():
    app = tk.Tk()
    app2 = ProjectWindow(app)
    app.mainloop()


if __name__ == "__main__":
    main()
