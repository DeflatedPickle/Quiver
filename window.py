#!/usr/bin/env python
"""The main window for Quiver."""

import tkinter as tk
import _tkinter
from tkinter import ttk
from PIL import Image, ImageTk
import json
import os
import subprocess
import platform
from datetime import datetime

import pkinter as pk

import load_images
import project_window
import highlightingtext
import about_window

# http://minecraft.gamepedia.com/Programs_and_editors/Resource_pack_creators
# http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1265199-tool-resourcepack-workbench-resource-pack-creator
# http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1265980-windows-version-1-0-8-minecraft-texture-studio

# http://minecraft.gamepedia.com/Formatting_codes


class Window(tk.Tk):
    def __init__(self, *args, **kwargs):
        tk.Tk.__init__(self, *args, **kwargs)
        self.title("Quiver")
        image = load_images.LoadImages()

        self.iconphoto(self._w, image.icon)
        self.geometry("700x500")
        self.minsize(width=500, height=300)
        self.rowconfigure(1, weight=1)
        self.columnconfigure(0, weight=1)

        self.operating_system = platform.system()

        self.program_font_type = "Courier"
        self.program_font_size = 10

        self.image_folder_close = image.image_folder_close
        self.image_folder_open = image.image_folder_open
        self.image_painting = image.image_painting
        self.image_paper_text = image.image_paper_text
        self.image_paper_json = image.image_paper_json
        self.image_paper_binary = image.image_paper_binary
        self.image_paper_language = image.image_paper_language
        self.image_cube = image.image_cube
        self.image_fragment = image.image_fragment
        self.image_vertex = image.image_vertex
        self.image_nbt = image.image_nbt

        self.image_exit = image.image_exit

        self.image_refresh = image.image_refresh

        self.image_find = image.image_find

        self.cmd = Commands(self)
        self.load_properties()

        self.style = ttk.Style()
        # print(self.style.theme_names())
        try:
            self.style.theme_use(self.properties["theme"])
        except _tkinter.TclError:
            print("{} | TclError: '{}' is not a valid style,"
                  " using system native instead.".format(datetime.now().strftime("%H:%M:%S"), self.properties["theme"]))
        except AttributeError:
            pass

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.status_bar = Statusbar(self)
        self.status_bar.grid(row=2, column=0, sticky="we")

        self.widget_paned_window = ttk.PanedWindow(self, orient="horizontal")
        self.widget_paned_window.grid(row=1, column=0, sticky="nesw")

        self.widget_frame_tree = ttk.Frame(self.widget_paned_window)
        self.widget_frame_tree.rowconfigure(0, weight=1)
        self.widget_frame_tree.columnconfigure(0, weight=1)
        # self.widget_frame_tree.grid(row=0, column=0, sticky="nesw")
        self.widget_paned_window.add(self.widget_frame_tree, weight=1)

        self.widget_tree = Tree(self.widget_frame_tree, self)
        self.widget_tree.grid(row=0, column=0, sticky="nesw")
        self.widget_tree.focus_set()

        self.widget_tree.tag_configure("Directory", font=("", "10", "bold"))

        self.scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_tree,
                                                  orient="horizontal",
                                                  command=self.widget_tree.xview)
        self.scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.scrollbar_vertical = ttk.Scrollbar(self.widget_frame_tree,
                                                orient="vertical",
                                                command=self.widget_tree.yview)
        self.scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_tree.configure(xscrollcommand=self.scrollbar_horizontal.set,
                                   yscrollcommand=self.scrollbar_vertical.set)

        self.widget_frame_panel = ttk.Frame(self.widget_paned_window)
        self.widget_frame_panel.rowconfigure(0, weight=1)
        self.widget_frame_panel.columnconfigure(0, weight=1)
        self.widget_paned_window.add(self.widget_frame_panel, weight=0)

        self.widget_panel = SidePanel(self.widget_frame_panel, tree=self.widget_tree, window=self)

        self.widget_tree.bind("<<TreeviewSelect>>", self.show_side_panel)
        self.widget_tree.bind("<Double-Button-1>", self.open_file)
        self.widget_tree.bind("<Return>", self.open_file)
        self.widget_tree.bind("<<TreeviewOpen>>", self.widget_tree.open_folder)
        self.widget_tree.bind("<<TreeviewClose>>", self.widget_tree.close_folder)

        # self.cmd.tree_refresh()

    def show_side_panel(self, *args):
        if self.widget_tree.item(self.widget_tree.focus())["tags"][0] != "Directory":
            self.widget_panel.update_variables()
            self.widget_panel.widget_frame_image.grid_forget()
            self.widget_panel.widget_frame_code.grid_forget()

            self.widget_panel.grid(row=0, column=0, sticky="nesw")
            self.widget_paned_window.pane(self.widget_frame_panel, weight=1)

            extension = self.widget_tree.item(self.widget_tree.focus())["values"][1]

            if extension == ".png":
                self.widget_panel.frame_image()

            elif extension == ".txt" or extension == ".json" or extension == ".mcmeta":
                self.widget_panel.frame_code()

        else:
            self.widget_panel.grid_forget()
            self.widget_paned_window.pane(self.widget_frame_panel, weight=0)

    def open_file(self, *args):
        if self.widget_tree.item(self.widget_tree.focus())["tags"][0] != "Directory":
            file = self.widget_tree.item(self.widget_tree.focus())["tags"][0]
            if self.operating_system == "Windows":
                os.startfile(file)
            else:
                opener = "open" if self.operating_system == "Darwin" else "xdg-open"
                subprocess.call([opener, file])

    def load_properties(self):
        try:
            with open("properties.json", "r") as file:
                self.properties = json.loads(file.read())
                file.close()
        except FileNotFoundError:
            print("{} | FileNotFoundError: 'properties.json' not found.".format(datetime.now().strftime("%H:%M:%S")))

    def write_properties(self, key, value):
        with open("properties.json", "w+") as file:
            self.properties[key] = value
            print(self.properties)
            file.write(json.dumps(self.properties, sort_keys=False, indent=2))
            file.close()


class Tree(ttk.Treeview):
    def __init__(self, parent, window, *args, **kwargs):
        ttk.Treeview.__init__(self, parent, selectmode="browse", columns=["", ""], *args, **kwargs)
        self.parent = window

        self.heading("#0", text="Resource File")
        self.heading("#1", text="Files")
        self.column("#1", anchor="e", width=50, stretch=False)
        self.heading("#2", text="File Extension")
        self.column("#2", width=100, stretch=False)

        self.widget_menu_tree = tk.Menu(self)
        self.bind("<Button-3>", self.show_menu)

        self.widget_menu_tree.add_command(label="Open", command=self.parent.open_file)
        self.widget_menu_tree.add_command(label="Edit", state="disabled")
        self.widget_menu_tree.add_separator()
        self.widget_menu_tree.add_command(label="Delete", command=self.parent.cmd.tree_delete_selected)

    def open_folder(self, *args):
        if self.item(self.focus())["image"][0] == "pyimage2":
            self.item(self.focus(), image=self.parent.image_folder_open)

    def close_folder(self, *args):
        if self.item(self.focus())["image"][0] == "pyimage3":
            self.item(self.focus(), image=self.parent.image_folder_close)

    def show_menu(self, event):
        # name = self.item(self.identify("item", event.x, event.y))["text"]
        # extension = self.item(self.identify("item", event.x, event.y))["values"][1]

        self.widget_menu_tree.post(event.x_root, event.y_root)


class SidePanel(ttk.Frame):
    def __init__(self, parent, tree: Tree, window, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.tree = tree
        self.window = window
        self.rowconfigure(2, weight=1)
        self.columnconfigure(0, weight=1)

        ##################################################

        self.widget_frame_text = ttk.Frame(self)
        self.widget_frame_text.grid(row=0, column=0, sticky="nesw")
        self.widget_frame_text.rowconfigure(0, weight=1)
        self.widget_frame_text.columnconfigure(1, weight=1)

        self.file_variable = tk.StringVar()
        ttk.Label(self.widget_frame_text, text="File Name:").grid(row=0, column=0, sticky="w")
        ttk.Label(self.widget_frame_text, textvariable=self.file_variable).grid(row=0, column=1, sticky="w")

        self.extension_variable = tk.StringVar()
        ttk.Label(self.widget_frame_text, text="File Extension:").grid(row=1, column=0, sticky="w")
        ttk.Label(self.widget_frame_text, textvariable=self.extension_variable).grid(row=1, column=1, sticky="w")

        self.description_variable = tk.StringVar()
        ttk.Label(self.widget_frame_text, text="File Description:").grid(row=2, column=0, sticky="w")
        ttk.Label(self.widget_frame_text, textvariable=self.description_variable).grid(row=2, column=1, sticky="w")

        self.image_size_variable = tk.StringVar()
        self.image_size_label = ttk.Label(self.widget_frame_text, text="Image Size:")
        self.image_size_variable_label = ttk.Label(self.widget_frame_text, textvariable=self.image_size_variable)

        ##################################################

        self.widget_frame_code_buttons = ttk.Frame(self)
        self.widget_frame_code_buttons.grid(row=1, column=0, columnspan=2, sticky="we")

        self.widget_button_open = ttk.Button(self.widget_frame_code_buttons, text="Open", command=self.window.open_file)
        self.widget_button_open.grid(row=0, column=0)

        self.widget_button_edit = ttk.Button(self.widget_frame_code_buttons, text="Edit", state="disabled")
        self.widget_button_edit.grid(row=0, column=1)

        ##################################################

        self.widget_frame_image = ttk.Frame(self)
        self.widget_frame_image.rowconfigure(1, weight=1)
        self.widget_frame_image.columnconfigure(0, weight=1)

        self.widget_frame_image_checks = ttk.Frame(self.widget_frame_image)
        self.widget_frame_image_checks.grid(row=0, column=0, columnspan=2, sticky="we")

        self.variable_show_chessboard = tk.BooleanVar()
        self.variable_show_chessboard.set(False)
        self.widget_check_chessboard = ttk.Checkbutton(self.widget_frame_image_checks, text="Hide Chessboard",
                                                       variable=self.variable_show_chessboard, command=self.frame_image)
        self.widget_check_chessboard.grid(row=0, column=0)

        self.variable_show_grid = tk.BooleanVar()
        self.variable_show_grid.set(False)
        self.widget_check_grid = ttk.Checkbutton(self.widget_frame_image_checks, text="Hide Grid",
                                                 variable=self.variable_show_grid, command=self.frame_image)
        self.widget_check_grid.grid(row=0, column=1)

        self.widget_frame_texture = ttk.Frame(self.widget_frame_image)
        self.widget_frame_texture.grid(row=1, column=0, sticky="nesw")
        self.widget_frame_texture.rowconfigure(0, weight=1)
        self.widget_frame_texture.columnconfigure(0, weight=1)

        self.widget_canvas = tk.Canvas(self.widget_frame_texture, width=256, height=256)
        self.widget_canvas.grid(row=0, column=0, sticky="nesw")

        self.widget_canvas_scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_texture,
                                                                orient="horizontal",
                                                                command=self.widget_canvas.xview)
        self.widget_canvas_scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.widget_canvas_scrollbar_vertical = ttk.Scrollbar(self.widget_frame_texture,
                                                              orient="vertical",
                                                              command=self.widget_canvas.yview)
        self.widget_canvas_scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_canvas.configure(xscrollcommand=self.widget_canvas_scrollbar_horizontal.set,
                                     yscrollcommand=self.widget_canvas_scrollbar_vertical.set)

        ##################################################

        self.widget_frame_code = ttk.Frame(self)
        self.widget_frame_code.rowconfigure(2, weight=1)
        self.widget_frame_code.columnconfigure(0, weight=1)

        self.widget_frame_code_checks = ttk.Frame(self.widget_frame_code)
        self.widget_frame_code_checks.grid(row=0, column=0, columnspan=2, sticky="we")

        self.variable_show_signs = tk.BooleanVar()
        self.variable_show_signs.set(False)
        self.widget_check_signs = ttk.Checkbutton(self.widget_frame_code_checks, text="Hide Section Signs",
                                                  variable=self.variable_show_signs,
                                                  command=lambda: self.widget_text.tag_configure(
                                                      "section sign", elide=self.variable_show_signs.get()))
        self.widget_check_signs.grid(row=0, column=0)

        self.widget_frame_code_text = ttk.Frame(self.widget_frame_code)
        self.widget_frame_code_text.grid(row=2, column=0, sticky="nesw")
        self.widget_frame_code_text.rowconfigure(0, weight=1)
        self.widget_frame_code_text.columnconfigure(1, weight=1)

        self.widget_text = highlightingtext.HighlightingText(self.widget_frame_code_text, wrap="none", width=32, height=0)
        self.widget_text.grid(row=0, column=1, sticky="nesw")
        self.configure_tags()

        self.widget_text_scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_code_text,
                                                              orient="horizontal",
                                                              command=self.widget_text.xview)
        self.widget_text_scrollbar_horizontal.grid(row=1, column=1, sticky="we")

        self.widget_text_scrollbar_vertical = ttk.Scrollbar(self.widget_frame_code_text,
                                                            orient="vertical",
                                                            command=self.widget_text.yview)
        self.widget_text_scrollbar_vertical.grid(row=0, column=2, sticky="ns")

        self.widget_text_line_numbers = pk.LineNumbers(self.widget_frame_code_text,
                                                       text_widget=self.widget_text,
                                                       scroll_widget=self.widget_text_scrollbar_vertical)
        self.widget_text_line_numbers.grid(row=0, column=0, sticky="ns")

        self.widget_text.configure(xscrollcommand=self.widget_text_scrollbar_horizontal.set,
                                   yscrollcommand=self.widget_text_scrollbar_vertical.set)

    def update_variables(self):
        self.file_variable.set(self.tree.item(self.tree.focus())["text"])
        self.extension_variable.set(self.tree.item(self.tree.focus())["values"][1])
        self.description_variable.set(self.tree.item(self.tree.focus())["values"][2])

    def frame_image(self):
        self.widget_frame_image.grid(row=2, column=0, sticky="nesw")
        self.image_size_label.grid(row=3, column=0, sticky="w")
        self.image_size_variable_label.grid(row=3, column=1, sticky="w")
        self.widget_canvas.delete("all")

        image_file = " ".join(self.tree.item(self.tree.focus())["tags"])

        image = Image.open(image_file)
        self.image = ImageTk.PhotoImage(image)

        if self.tree.item(self.tree.focus())["text"] == "pack":
            width = 16
            height = 16
            times = 2

        else:
            width = self.image.width()
            height = self.image.height()
            times = 16

        self.image = ImageTk.PhotoImage(image.resize((self.image.width() * times, self.image.height() * times)))
        self.image_size_variable.set("{}x{}".format(self.image.width() // times, self.image.height() // times))

        self.widget_canvas.configure(scrollregion=(0, 0, self.image.width(), self.image.height()))

        if not self.variable_show_chessboard.get():
            colour1 = "white"
            colour2 = "light grey"
            colour = colour2
            for row in range(height):
                colour = colour1 if colour == colour2 else colour2
                for col in range(width):
                    x1 = (col * 16)
                    y1 = (row * 16)
                    x2 = x1 + 16
                    y2 = y1 + 16
                    self.widget_canvas.create_rectangle(x1, y1, x2, y2, outline=colour, fill=colour)
                    colour = colour1 if colour == colour2 else colour2

        self.widget_canvas.create_image(0, 0, anchor="nw", image=self.image)

        if not self.variable_show_grid.get():
            colour3 = "light grey"
            for row in range(height):
                colour = colour3
                for col in range(width):
                    x1 = (col * 16)
                    y1 = (row * 16)
                    x2 = x1 + 16
                    y2 = y1 + 16
                    self.widget_canvas.create_rectangle(x1, y1, x2, y2, outline=colour, fill=None)
                    colour = colour

    def frame_code(self):
        self.widget_frame_code.grid(row=2, column=0, sticky="nesw")
        self.image_size_label.grid_forget()
        self.image_size_variable_label.grid_forget()
        self.widget_text.configure(state="normal")
        self.widget_text.delete(1.0, "end")

        code_file = " ".join(self.tree.item(self.tree.focus())["tags"])

        with open(code_file, encoding="utf8") as file:
            self.widget_text.insert(1.0, file.read())
            file.close()

        self.widget_text_line_numbers.redraw()
        self.widget_text.highlight_until('"', '"', "string")
        self.widget_text.highlight_pattern("{", "curly bracket")
        self.widget_text.highlight_pattern("}", "curly bracket")
        self.widget_text.highlight_pattern("[", "square bracket")
        self.widget_text.highlight_pattern("]", "square bracket")
        self.widget_text.highlight_pattern(",", "comma")
        self.widget_text.highlight_pattern(":", "colon")
        for i in range(10):
            self.widget_text.highlight_pattern(str(i), "number")
        self.widget_text.highlight_pattern("true", "boolean")
        self.widget_text.highlight_pattern("false", "boolean")

        self.widget_text.highlight_until("§0", "§", "black")
        self.widget_text.highlight_until("§1", "§", "dark blue")
        self.widget_text.highlight_until("§2", "§", "dark green")
        self.widget_text.highlight_until("§3", "§", "dark aqua")
        self.widget_text.highlight_until("§4", "§", "dark red")
        self.widget_text.highlight_until("§5", "§", "dark purple")
        self.widget_text.highlight_until("§6", "§", "gold")
        self.widget_text.highlight_until("§7", "§", "gray")
        self.widget_text.highlight_until("§8", "§", "dark gray")
        self.widget_text.highlight_until("§9", "§", "blue")
        self.widget_text.highlight_until("§a", "§", "green")
        self.widget_text.highlight_until("§b", "§", "aqua")
        self.widget_text.highlight_until("§c", "§", "red")
        self.widget_text.highlight_until("§d", "§", "light purple")
        self.widget_text.highlight_until("§e", "§", "yellow")
        self.widget_text.highlight_until("§f", "§", "white")

        self.widget_text.highlight_until("§l", "§", "bold")
        self.widget_text.highlight_until("§m", "§", "strikethrough")
        self.widget_text.highlight_until("§n", "§", "underline")
        self.widget_text.highlight_until("§o", "§", "italic")
        self.widget_text.highlight_until("§r", "§", "reset")
        self.widget_text.configure(state="disabled")

    def configure_tags(self):
        self.widget_text.tag_configure("string", foreground="lime green")
        self.widget_text.tag_configure("curly bracket", foreground="dark orange")
        self.widget_text.tag_configure("square bracket", foreground="deep pink")
        self.widget_text.tag_configure("comma", foreground="SlateBlue2")
        self.widget_text.tag_configure("colon", foreground="purple2")
        self.widget_text.tag_configure("number", foreground="purple4")
        self.widget_text.tag_configure("boolean", foreground="cyan3")

        self.widget_text.tag_configure("section sign", elide=self.variable_show_signs.get())

        self.widget_text.tag_configure("black", foreground="#000000")
        self.widget_text.tag_configure("dark blue", foreground="#0000AA")
        self.widget_text.tag_configure("dark green", foreground="#00AA00")
        self.widget_text.tag_configure("dark aqua", foreground="#00AAAA")
        self.widget_text.tag_configure("dark red", foreground="#AA0000")
        self.widget_text.tag_configure("dark purple", foreground="#AA00AA")
        self.widget_text.tag_configure("gold", foreground="#FFAA00")
        self.widget_text.tag_configure("gray", foreground="#AAAAAA")
        self.widget_text.tag_configure("dark gray", foreground="#555555")
        self.widget_text.tag_configure("blue", foreground="#5555FF")
        self.widget_text.tag_configure("green", foreground="#55FF55")
        self.widget_text.tag_configure("aqua", foreground="#55FFFF")
        self.widget_text.tag_configure("red", foreground="#FF5555")
        self.widget_text.tag_configure("light purple", foreground="#FF55FF")
        self.widget_text.tag_configure("yellow", foreground="#FFFF55")
        self.widget_text.tag_configure("white", foreground="#FFFFFF")

        self.widget_text.tag_configure("bold", font=(self.window.program_font_type, self.window.program_font_size,
                                                     "bold"))
        self.widget_text.tag_configure("strikethrough", font=(self.window.program_font_type,
                                                              self.window.program_font_size,
                                                              "overstrike"))
        self.widget_text.tag_configure("underline", font=(self.window.program_font_type, self.window.program_font_size,
                                                          "underline"))
        self.widget_text.tag_configure("italic", font=(self.window.program_font_type, self.window.program_font_size,
                                                       "italic"))
        self.widget_text.tag_configure("reset", font=(self.window.program_font_type, self.window.program_font_size, ""))


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent

        self.init_menu_application()
        self.init_menu_file()
        # self.init_menu_edit()
        self.init_menu_view()
        self.init_menu_window()
        self.init_menu_help()
        self.init_menu_system()

        self.parent.configure(menu=self)

    def init_menu_application(self):
        self.menu_application = tk.Menu(self, name="apple")

        self.menu_application.add_command(label="About Quiver", command=lambda: about_window.AboutWindow(self.parent))
        self.menu_application.add_command(label="Exit", image=self.parent.image_exit, compound="left",
                                          command=self.parent.cmd.exit_program)

        self.add_cascade(label="Application", menu=self.menu_application)

    def init_menu_file(self):
        self.menu_file = tk.Menu(self)

        self.menu_file.add_command(label="Open Project File", image=self.parent.image_folder_open, compound="left",
                                   command=lambda: os.startfile(self.parent.directory))

        self.add_cascade(label="File", menu=self.menu_file)

    def init_menu_edit(self):
        self.menu_edit = tk.Menu(self)

        self.add_cascade(label="Edit", menu=self.menu_edit)

    def init_menu_view(self):
        self.menu_view = tk.Menu(self)

        self.menu_view.add_command(label="Collapse the TreeView", command=self.parent.cmd.tree_collapse)
        self.menu_view.add_command(label="Expand the TreeView", command=self.parent.cmd.tree_expand)
        self.menu_view.add_command(label="Refresh the TreeView", image=self.parent.image_refresh, compound="left",
                                   command=self.parent.cmd.tree_refresh)

        self.add_cascade(label="View", menu=self.menu_view)

    def init_menu_window(self):
        self.menu_window = tk.Menu(self, name="window")
        self.add_cascade(label="Window", menu=self.menu_window)

    def init_menu_help(self):
        self.menu_help = tk.Menu(self, name="help")

        self.add_cascade(label="Help", menu=self.menu_help)

    def init_menu_system(self):
        self.menu_system = tk.Menu(self, name="system")
        self.add_cascade(label="System", menu=self.menu_system)


class Toolbar(ttk.Frame):
    def __init__(self, parent, *args, **kwargs):
        ttk.Frame.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.columnconfigure(6, weight=1)

        # self.widget_button_undo = ttk.Button(self, text="Undo", style="Toolbutton")
        # self.widget_button_undo.grid(row=0, column=0)

        # self.widget_button_redo = ttk.Button(self, text="Redo", style="Toolbutton")
        # self.widget_button_redo.grid(row=0, column=1)

        # ttk.Separator(self, orient="vertical").grid(row=0, column=2, sticky="ns")

        self.widget_button_refresh = ttk.Button(self, text="Refresh", image=self.parent.image_refresh,
                                                command=self.parent.cmd.tree_refresh,
                                                style="Toolbutton")
        self.widget_button_refresh.grid(row=0, column=3)

        self.widget_entry_search = ttk.Entry(self)
        self.widget_entry_search.grid(row=0, column=6, sticky="we")

        self.widget_button_previous = ttk.Button(self, text="Previous", state="disabled")
        self.widget_button_previous.grid(row=0, column=7)

        self.widget_button_next = ttk.Button(self, text="Next", state="disabled")
        self.widget_button_next.grid(row=0, column=8)

        self.widget_button_search = ttk.Button(self, text="Search", command=self.parent.cmd.search)
        self.widget_button_search.grid(row=0, column=9)

        self.widget_button_exit = ttk.Button(self, text="Exit", image=self.parent.image_exit,
                                             command=self.parent.cmd.exit_program,
                                             style="Toolbutton")
        self.widget_button_exit.grid(row=0, column=10, sticky="e")


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args, **kwargs):
        pk.Statusbar.__init__(self, parent, *args, **kwargs)

        self.status_variable = tk.StringVar()
        self.add_variable(textvariable=self.status_variable)

        self.bind_menu(parent.menu, self.status_variable, ["", "", "", "", ""])
        self.bind_menu(parent.menu.menu_application, self.status_variable, ["Exit the program"])
        self.bind_menu(parent.menu.menu_file, self.status_variable, ["Open the project folder"])
        # self.bind_menu(parent.menu.menu_edit, self.status_variable, ["Undo the last action",
        #                                                              "Redo the last action"])
        self.bind_menu(parent.menu.menu_view, self.status_variable, ["Collapse the items in the TreeView",
                                                                     "Expand the items in the TreeView",
                                                                     "Refresh the items in the TreeView"])
        # self.bind_widget(parent.toolbar.widget_button_undo, self.status_variable, "Undo the last action", "")
        # self.bind_widget(parent.toolbar.widget_button_redo, self.status_variable, "Redo the last action", "")
        self.bind_widget(parent.toolbar.widget_button_refresh, self.status_variable,
                         "Refresh the files in the TreeView", "")
        self.bind_widget(parent.toolbar.widget_button_previous, self.status_variable,
                         "Search for the previous instance", "")
        self.bind_widget(parent.toolbar.widget_button_next, self.status_variable,
                         "Search for the next instance", "")
        self.bind_widget(parent.toolbar.widget_button_exit, self.status_variable, "Exit the program", "")

        self.add_sizegrip()


class Commands:
    def __init__(self, parent):
        self.parent = parent

    def tree_collapse(self):
        for i in self.parent.widget_tree.get_children():
            self.parent.widget_tree.item(i, open=False)

            if self.parent.widget_tree.item(i)["image"][0] == "pyimage3":
                self.parent.widget_tree.item(i, image=self.parent.image_folder_close)

    def tree_expand(self):
        for i in self.parent.widget_tree.get_children():
            self.parent.widget_tree.item(i, open=True)

            if self.parent.widget_tree.item(i)["image"][0] == "pyimage2":
                self.parent.widget_tree.item(i, image=self.parent.image_folder_open)

    def tree_refresh(self):
        self.parent.widget_tree.delete(*self.parent.widget_tree.get_children())
        self.parent.widget_tree.insert(parent="",
                                       index="end",
                                       iid=self.parent.directory,
                                       text=self.parent.directory.split("/")[-1:],
                                       image=self.parent.image_folder_open,
                                       tags="Directory")
        # self.widget_tree.selection_set()

        variable = 0
        for root, dirs, files in os.walk(self.parent.directory, topdown=True):
            for name in dirs:
                self.parent.widget_tree.insert(parent=root,
                                               index="end",
                                               iid=os.path.join(root, name),
                                               text=name,
                                               values=("", ""),
                                               image=self.parent.image_folder_close,
                                               tags=("Directory", os.path.join(root, name).replace("\\", "/")))
                variable += 1

                self.parent.widget_tree.item(root, values=(variable, ""))
                # print(os.path.join(root, name))

            variable = 0
            for name in files:
                insert = self.parent.widget_tree.insert(parent=root,
                                                        index="end",
                                                        text=os.path.splitext(name)[0],
                                                        values=("", os.path.splitext(name)[1], ""),
                                                        tags=os.path.join(root, name).replace("\\", "/"))
                variable += 1

                item = self.parent.widget_tree.item(insert)["values"][1]

                if item == ".png":
                    self.parent.widget_tree.item(insert, image=self.parent.image_painting)

                elif item == ".mcmeta":
                    self.parent.widget_tree.item(insert, image=self.parent.image_cube)

                elif item == ".txt":
                    self.parent.widget_tree.item(insert, image=self.parent.image_paper_text)

                elif item == ".json":
                    self.parent.widget_tree.item(insert, image=self.parent.image_paper_json)

                elif item == ".bin":
                    self.parent.widget_tree.item(insert, image=self.parent.image_paper_binary)

                elif item == ".lang":
                    self.parent.widget_tree.item(insert, image=self.parent.image_paper_language)

                elif item == ".fsh":
                    self.parent.widget_tree.item(insert, image=self.parent.image_fragment)

                elif item == ".vsh":
                    self.parent.widget_tree.item(insert, image=self.parent.image_vertex)

                elif item == ".nbt":
                    self.parent.widget_tree.item(insert, image=self.parent.image_nbt)

                self.parent.widget_tree.item(root, values=(variable, ""))

                # print(root)

        for child in self.parent.widget_tree.get_children():
            if self.parent.widget_tree.item(child)["tags"][0] == "Directory":
                self.parent.widget_tree.item(child, open=True)

    def tree_delete_selected(self):
        try:
            item = self.parent.widget_tree.item(self.parent.widget_tree.focus())["tags"][0]
            os.remove(item)
            print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), item))
        except FileNotFoundError:
            for root, dirs, files in os.walk(self.parent.widget_tree.item(self.parent.widget_tree.focus())["tags"][1],
                                             topdown=False):
                for name in files:
                    os.remove(os.path.join(root, name))
                    print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), name))
                for name in dirs:
                    os.rmdir(os.path.join(root, name))
                    print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), name))
            item = self.parent.widget_tree.item(self.parent.widget_tree.focus())["tags"][1]
            os.rmdir(item)
            print("{} | Deleting: {}".format(datetime.now().strftime("%H:%M:%S"), item))
        self.parent.cmd.tree_refresh()

    def tree_copy_selected(self):
        pass

    def search(self, item=""):
        children = self.parent.widget_tree.get_children(item)
        for child in children:
            text = self.parent.widget_tree.item(child, "text") + self.parent.widget_tree.item(child, "values")[1]
            if text.startswith(self.parent.toolbar.widget_entry_search.get()):
                self.parent.widget_tree.focus(child)
                self.parent.widget_tree.selection_set(child)
                self.parent.widget_tree.see(child)
                return True
            else:
                res = self.search(child)
                if res:
                    return True

    def exit_program(self):
        raise SystemExit


def main():
    app = Window()
    project_window.ProjectWindow(app)
    # cmd = Commands(app)
    # app.load_files()
    # cmd.tree_refresh()
    app.mainloop()


if __name__ == "__main__":
    main()
