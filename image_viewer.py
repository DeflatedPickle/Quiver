#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""The image-viewer for Quiver."""

import tkinter as tk
from tkinter import ttk
import idlelib.ToolTip
import os

import pkinter as pk
from PIL import Image, ImageTk

import load_images

__title__ = "Painting"
__author__ = "DeflatedPickle"
__version__ = "1.11.1"


class ImageViewer(tk.Toplevel):
    def __init__(self, parent, *args, **kwargs):
        tk.Toplevel.__init__(self, parent, *args, **kwargs)
        self.parent = parent
        self.title("Painting")
        self.geometry("500x400")
        self.minsize(width=300, height=200)
        self.maxsize(width=1000, height=800)
        # self.transient(parent)
        self.rowconfigure(1, weight=1)
        self.columnconfigure(0, weight=1)

        self.image_open = None
        self.image_photo = None
        self.drawn_image = None

        self.original_width = 0
        self.original_height = 0

        self.zoom_width = 0
        self.zoom_height = 0

        self.zoom_speed = 16
        self.zoom_current = 1

        self.scroll_past_horizontally = 50
        self.scroll_past_vertically = 50

        self.menu = Menu(self)

        self.toolbar = Toolbar(self)
        self.toolbar.grid(row=0, column=0, sticky="we")

        self.statusbar = Statusbar(self)
        self.statusbar.grid(row=2, column=0, sticky="we")

        ##################################################

        self.widget_frame_image = ttk.Frame(self)
        self.widget_frame_image.grid(row=1, column=0, sticky="nesw")
        self.widget_frame_image.rowconfigure(0, weight=1)
        self.widget_frame_image.columnconfigure(0, weight=1)
        self.widget_frame_image.bind_all("<Control-MouseWheel>", self.zoom_handler)
        self.widget_frame_image.bind_all("<Control-Button-4>", self.zoom_handler)
        self.widget_frame_image.bind_all("<Control-Button-5>", self.zoom_handler)
        self.widget_frame_image.bind_all("<Enter>", self.on_enter)
        self.widget_frame_image.bind_all("<Leave>", self.on_leave)

        self.widget_canvas_image = tk.Canvas(self.widget_frame_image)
        self.widget_canvas_image.grid(row=0, column=0)

        self.widget_scrollbar_horizontal = ttk.Scrollbar(self.widget_frame_image, orient="horizontal",
                                                         command=self.widget_canvas_image.xview)
        self.widget_scrollbar_horizontal.grid(row=1, column=0, sticky="we")

        self.widget_scrollbar_vertical = ttk.Scrollbar(self.widget_frame_image, orient="vertical",
                                                       command=self.widget_canvas_image.yview)
        self.widget_scrollbar_vertical.grid(row=0, column=1, sticky="ns")

        self.widget_canvas_image.configure(xscrollcommand=self.widget_scrollbar_horizontal.set,
                                           yscrollcommand=self.widget_scrollbar_vertical.set)

        self.check_zoom()

    def zoom_handler(self, event):
        if event.delta == 120 or event.num == 4:
            if self.zoom_current < 16:
                self.zoom_in()

        elif event.delta == -120:
            if self.zoom_current > 1 or event.num == 5:
                self.zoom_out()

    def load_image(self, image=""):
        self.widget_canvas_image.delete("all")

        self.image_open = Image.open(image, "r")
        self.image_photo = ImageTk.PhotoImage(self.image_open)

        self.original_width = self.image_photo.width()
        self.original_height = self.image_photo.height()

        self.widget_canvas_image.configure(scrollregion=(0, 0, self.image_photo.width(), self.image_photo.height()))
        self.widget_canvas_image.configure(width=self.image_photo.width(), height=self.image_photo.height())
        self.drawn_image = self.widget_canvas_image.create_image(0, 0, anchor="nw", image=self.image_photo,
                                                                 tags="image")
        self.title("{} - {}".format(self.title(), "".join(os.path.splitext(image))))

        self.check_tile_buttons()
        self.draw_background()

    def draw_background(self):
        self.widget_canvas_image.delete("chessboard")
        self.widget_canvas_image.delete("grid")

        if self.toolbar.variable_chessboard.get():
            colour1 = "white"
            colour2 = "light grey"
            colour = colour2
            for row in range(self.original_height - (16 - self.zoom_current) + 1):
                colour = colour1 if colour == colour2 else colour2
                for col in range(self.original_width - (16 - self.zoom_current) + 1):
                    # print(self.zoom_current)
                    x1 = (col * 16)
                    y1 = (row * 16)
                    x2 = x1 + 16
                    y2 = y1 + 16
                    self.widget_canvas_image.create_rectangle(x1, y1, x2, y2, outline=colour, fill=colour,
                                                              tags="chessboard")
                    colour = colour1 if colour == colour2 else colour2

        # self.widget_canvas_image.lift(self.drawn_image)
        self.widget_canvas_image.lift("image")

        if self.toolbar.variable_grid.get():
            colour3 = "light grey"
            for row in range(self.original_height - (16 - self.zoom_current) + 1):
                colour = colour3
                for col in range(self.original_width - (16 - self.zoom_current) + 1):
                    # print(self.zoom_current)
                    x1 = (col * 16)
                    y1 = (row * 16)
                    x2 = x1 + 16
                    y2 = y1 + 16
                    self.widget_canvas_image.create_rectangle(x1, y1, x2, y2, outline=colour, fill=None, tags="grid")
                    colour = colour

    def zoom_in(self):
        self.widget_canvas_image.delete("image")

        self.image_photo = ImageTk.PhotoImage(self.image_open.resize(
            (self.image_photo.width() + self.original_width, self.image_photo.height() + self.original_height)))
        self.widget_canvas_image.configure(scrollregion=(self.check_scrollregion()))
        self.widget_canvas_image.configure(width=self.check_size()[0], height=self.check_size()[1])
        # self.drawn_image = self.widget_canvas_image.create_image(0, 0, anchor="nw", image=self.image_photo, tags="image")

        self.zoom_current += 1
        # print(self.zoom_current)

        self.zoom_width = self.image_photo.width()
        self.zoom_height = self.image_photo.height()

        self.draw_tiles()
        self.draw_background()
        self.check_zoom()

    def zoom_out(self):
        self.widget_canvas_image.delete("image")

        self.image_photo = ImageTk.PhotoImage(self.image_open.resize(
            (self.image_photo.width() - self.original_width, self.image_photo.height() - self.original_height)))
        self.widget_canvas_image.configure(scrollregion=(self.check_scrollregion()))
        self.widget_canvas_image.configure(width=self.check_size()[0], height=self.check_size()[1])
        # self.drawn_image = self.widget_canvas_image.create_image(0, 0, anchor="nw", image=self.image_photo, tags="image")

        self.zoom_current -= 1
        # print(self.zoom_current)

        self.zoom_width = self.image_photo.width()
        self.zoom_height = self.image_photo.height()

        self.draw_tiles()
        self.draw_background()
        self.check_zoom()

    def check_scrollregion(self):
        if self.toolbar.variable_tile.get():
            return 0, 0, self.image_photo.width() * 3, self.image_photo.height() * 3

        else:
            return 0, 0, self.image_photo.width(), self.image_photo.height()

    def check_size(self):
        if self.toolbar.variable_tile.get():
            return self.image_photo.width() * 3, self.image_photo.height() * 3

        else:
            return self.image_photo.width(), self.image_photo.height()

    def check_zoom(self):
        if self.zoom_current > 15:
            self.toolbar.widget_button_zoom_in.configure(state="disabled")
        else:
            self.toolbar.widget_button_zoom_in.configure(state="enabled")

        if self.zoom_current < 2:
            self.toolbar.widget_button_zoom_out.configure(state="disabled")
        else:
            self.toolbar.widget_button_zoom_out.configure(state="enabled")

    def check_tile_buttons(self):
        if self.toolbar.variable_tile.get():
            self.toolbar.widget_button_tile_sides.configure(state="normal")
            self.toolbar.widget_button_tile_corners.configure(state="normal")

        elif not self.toolbar.variable_tile.get():
            self.toolbar.widget_button_tile_sides.configure(state="disabled")
            self.toolbar.widget_button_tile_corners.configure(state="disabled")

            self.widget_canvas_image.create_image(0, 0, anchor="nw", image=self.image_photo, tags="image")
            self.widget_canvas_image.configure(width=self.image_photo.width(), height=self.image_photo.height())

    def draw_tiles(self):
        for item in ["image", "image_top", "image_bottom", "image_left", "image_right", "image_top_left", "image_top_right", "image_bottom_left", "image_bottom_right"]:
            self.widget_canvas_image.delete(item)

        self.widget_canvas_image.create_image(self.image_photo.width(), self.image_photo.height(), anchor="nw", image=self.image_photo, tags="image")

        if self.toolbar.variable_tile_sides.get():
            # self.widget_canvas_image.move("image", self.image_photo.width(), self.image_photo.height())

            self.widget_canvas_image.create_image(self.image_photo.width(), 0, anchor="nw", image=self.image_photo, tags="image_top")
            self.widget_canvas_image.create_image(self.image_photo.width(), self.image_photo.height() * 2, anchor="nw", image=self.image_photo, tags="image_bottom")
            self.widget_canvas_image.create_image(0, self.image_photo.height(), anchor="nw", image=self.image_photo, tags="image_left")
            self.widget_canvas_image.create_image(self.image_photo.width() * 2, self.image_photo.height(), anchor="nw", image=self.image_photo, tags="image_right")

        if self.toolbar.variable_tile_corners.get():
            self.widget_canvas_image.create_image(0, 0, anchor="nw", image=self.image_photo, tags="image_top_left")
            self.widget_canvas_image.create_image(self.image_photo.width() * 2, 0, anchor="nw", image=self.image_photo, tags="image_top_right")
            self.widget_canvas_image.create_image(0, self.image_photo.height() * 2, anchor="nw", image=self.image_photo, tags="image_bottom_left")
            self.widget_canvas_image.create_image(self.image_photo.width() * 2, self.image_photo.height() * 2, anchor="nw", image=self.image_photo, tags="image_bottom_right")

        self.widget_canvas_image.configure(width=self.image_photo.width() * 3, height=self.image_photo.height() * 3)

    def on_enter(self, event):
        self.widget_frame_image.bind_all("<MouseWheel>", self.on_scroll_vertical)
        self.widget_frame_image.bind_all("<Shift-MouseWheel>", self.on_scroll_horizontal)

        del event

    def on_leave(self, event):
        self.widget_frame_image.unbind_all("<MouseWheel>")
        self.widget_frame_image.unbind_all("<Shift-MouseWheel>")

        del event

    def on_scroll_vertical(self, event):
        self.widget_canvas_image.yview_scroll(int(-1 * (event.delta / 120)), "units")

    def on_scroll_horizontal(self, event):
        self.widget_canvas_image.xview_scroll(int(-1 * (event.delta / 120)), "units")


class Menu(tk.Menu):
    def __init__(self, parent, *args, **kwargs):
        tk.Menu.__init__(self, parent, type="menubar", *args, **kwargs)
        self.option_add('*tearOff', False)
        self.parent = parent


class Toolbar(ttk.Frame):
    def __init__(self, parent, **kwargs):
        ttk.Frame.__init__(self, parent, **kwargs)
        self.parent = parent

        # TODO: Change this to a pk.Toolbar.

        image = load_images.LoadImages()
        self.image_chessboard = image.image_chessboard
        self.image_grid = image.image_grid
        self.image_zoom_in = image.image_zoom_in
        self.image_zoom_out = image.image_zoom_out

        self.variable_chessboard = tk.BooleanVar()
        self.variable_chessboard.set(True)
        self.widget_check_chessboard = ttk.Checkbutton(self, text="Chessboard", image=self.image_chessboard,
                                                       variable=self.variable_chessboard,
                                                       command=self.parent.draw_background, style="Toolbutton")
        self.widget_check_chessboard.grid(row=0, column=0)

        self.variable_grid = tk.BooleanVar()
        self.variable_grid.set(False)
        self.widget_check_grid = ttk.Checkbutton(self, text="Grid", image=self.image_grid, variable=self.variable_grid,
                                                 command=self.parent.draw_background, style="Toolbutton")
        self.widget_check_grid.grid(row=0, column=1)

        ttk.Separator(self, orient="vertical").grid(row=0, column=2, sticky="ns")

        self.widget_button_zoom_in = ttk.Button(self, text="Zoom In", image=self.image_zoom_in,
                                                command=self.parent.zoom_in, style="Toolbutton")
        self.widget_button_zoom_in.grid(row=0, column=3)

        self.widget_button_zoom_out = ttk.Button(self, text="Zoom Out", image=self.image_zoom_out,
                                                 command=self.parent.zoom_out, style="Toolbutton")
        self.widget_button_zoom_out.grid(row=0, column=4)
        idlelib.ToolTip.ToolTip(self.widget_button_zoom_out, "Zoom the image out")

        ttk.Separator(self, orient="vertical").grid(row=0, column=5, sticky="ns")

        self.variable_tile = tk.BooleanVar()
        self.widget_button_tile = ttk.Checkbutton(self, text="Tile", variable=self.variable_tile,
                                                  command=self.parent.check_tile_buttons, style="Toolbutton")
        self.widget_button_tile.grid(row=0, column=6)

        self.variable_tile_sides = tk.BooleanVar()
        self.widget_button_tile_sides = ttk.Checkbutton(self, text="Tile Side", variable=self.variable_tile_sides, command=self.parent.draw_tiles, style="Toolbutton")
        self.widget_button_tile_sides.grid(row=0, column=7)

        self.variable_tile_corners = tk.BooleanVar()
        self.widget_button_tile_corners = ttk.Checkbutton(self, text="Tile Corners", variable=self.variable_tile_corners, command=self.parent.draw_tiles, style="Toolbutton")
        self.widget_button_tile_corners.grid(row=0, column=8)


class Statusbar(pk.Statusbar):
    def __init__(self, parent, *args):
        pk.Statusbar.__init__(self, parent, *args)

        self.status_variable = tk.StringVar()
        self.add_variable(variable=self.status_variable)

        self.bind_widget(parent.toolbar.widget_check_chessboard, self.status_variable, "Show or hide the chessboard",
                         "")
        self.bind_widget(parent.toolbar.widget_check_grid, self.status_variable, "Show or hide the grid", "")

        self.bind_widget(parent.toolbar.widget_button_zoom_in, self.status_variable, "Zoom the image in", "")
        self.bind_widget(parent.toolbar.widget_button_zoom_out, self.status_variable, "Zoom the image out", "")

        self.add_sizegrip()


def main():
    app = tk.Tk()
    image_viewer = ImageViewer(app)
    image_viewer.load_image("./test_files/cobblestone.png")
    # image_viewer.load_image("./icons/nbt.png")
    app.mainloop()


if __name__ == "__main__":
    main()
