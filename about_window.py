#!/usr/bin/env python
"""The about window for Quiver."""

import tkinter as tk
from tkinter import ttk

__title__ = "Quiver"
__author__ = "DeflatedPickle"
__version__ = "0.8.9-alpha"


class AboutWindow(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("About {}".format(__title__))
        self.geometry("350x325")
        self.resizable(width=False, height=False)
        self.transient(parent)
        self.grab_set()

        self.widget_frame_text = ttk.Frame(self)
        self.widget_frame_text.pack(side="top", fill="both", expand=True)
        self.widget_frame_text.rowconfigure(0, weight=1)
        self.widget_frame_text.columnconfigure(0, weight=1)

        self.widget_text = tk.Text(self.widget_frame_text, wrap="none", width=0, height=0)
        self.widget_text.grid(row=0, column=0, sticky="nesw")

        self.widget_text_scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_text, orient="horizontal", command=self.widget_text.xview)
        self.widget_text_scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.widget_text_scrollbar_vertical = ttk.Scrollbar(self.widget_frame_text, orient="vertical", command=self.widget_text.yview)
        self.widget_text_scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_text.configure(xscrollcommand=self.widget_text_scrollbar_horizontal.set, yscrollcommand=self.widget_text_scrollbar_vertical.set)

        self.widget_text.insert("end",
"""{} v{}:
- Written/Created by {}.
- Inspired by ResourcePack Workbench.

Info:
- Programming Language: Python 3.
- GUI Toolkit: Tkinter.

Credits:
- MightyPork for ResourcePack Workbench.
- Fredrik Lundh for Tkinter.
- Alex Clark for Pillow.
- Hartmut Goebel, Martin Zibricky, David Cortesi and David Vierra for PyInstaller.
- GitHub for hosting the code.
- Oracle Corporation for VirtualBox (used to export the program on multiple OSs).
- dotPDN, LLC for Paint.NET (used to program make icons/textures).
- JetBrains for PyCharm (the IDE used).
- Mojang for Minecraft.
- Bryan Oakley on StackOverflow for occasional indirect help with code.""".format(__title__, __version__, __author__))

        self.widget_text.configure(state="disabled")

        self.widget_frame_buttons = ttk.Frame(self)
        self.widget_frame_buttons.pack(side="bottom", fill="x")

        self.widget_button_cancel = ttk.Button(self.widget_frame_buttons, text="Close", command=self.close).pack(
            side="right")

    def close(self):
        self.destroy()


def main():
    app = tk.Tk()
    app.__title__ = "Quiver"
    app.__author__ = "DeflatedPickle"
    app.__version__ = "0.0.0"
    AboutWindow(app)
    app.mainloop()


if __name__ == "__main__":
    main()
