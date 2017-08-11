#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""Loads the images Quiver uses."""

from datetime import datetime

from PIL import Image, ImageTk


class LoadImages:
    def __init__(self):
        self.icon = self.new_image("quiver.ico")

        self.image_folder_close = self.new_image("icons/folder_close.png")
        self.image_folder_open = self.new_image("icons/folder_open.png")

        self.image_painting = self.new_image("icons/painting_decorated.png")

        self.image_paper_text = self.new_image("icons/paper_text.png")
        self.image_paper_json = self.new_image("icons/paper_json.png")
        self.image_paper_binary = self.new_image("icons/paper_binary.png")
        self.image_paper_language = self.new_image("icons/paper_language.png")

        self.image_cube = self.new_image("icons/cube.png")
        self.image_fragment = self.new_image("icons/fragment_shaded.png")
        self.image_vertex = self.new_image("icons/vertex_shaded.png")
        self.image_nbt = self.new_image("icons/nbt.png")

        self.image_refresh = self.new_image("icons/reload_arrow.png")

        self.image_chessboard = self.new_image("icons/chessboard.png")
        self.image_grid = self.new_image("icons/grid.png")

        self.image_zoom_in = self.new_image("icons/zoom_in_shaded.png")
        self.image_zoom_out = self.new_image("icons/zoom_out_shaded.png")

        self.image_find = self.new_image("icons/find.png")
        self.image_find_next = self.new_image("icons/find_next.png")
        self.image_find_previous = self.new_image("icons/find_previous.png")

        self.image_replace = self.new_image("icons/replace.png")

        self.image_paste = self.new_image("icons/paste.png")
        self.image_copy = self.new_image("icons/copy.png")
        self.image_cut = self.new_image("icons/cut_2.png")
        self.image_delete = self.new_image("icons/delete.png")

        self.image_undo = self.new_image("icons/undo.png")
        self.image_redo = self.new_image("icons/redo.png")

        self.image_save = self.new_image("icons/save.png")

        self.image_exit = self.new_image("icons/exit.png")

    def new_image(self, image_file: str = ""):
        try:
            image_copy = Image.open(image_file)
            return ImageTk.PhotoImage(image_copy)
        except FileNotFoundError:
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), image_file))
            return ""
