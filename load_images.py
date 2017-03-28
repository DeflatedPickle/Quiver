#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""Loads the images Quiver uses."""

from datetime import datetime

from PIL import Image, ImageTk


class LoadImages:
    def __init__(self):
        try:
            icon = Image.open("quiver.ico")
            self.icon = ImageTk.PhotoImage(icon)
        except FileNotFoundError:
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "quiver.ico"))

        try:
            image_folder_close = Image.open("icons/folder_close.png")
            self.image_folder_close = ImageTk.PhotoImage(image_folder_close)
        except FileNotFoundError:
            self.image_folder_close = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "folder_close.png"))

        try:
            image_folder_open = Image.open("icons/folder_open.png")
            self.image_folder_open = ImageTk.PhotoImage(image_folder_open)
        except FileNotFoundError:
            self.image_folder_open = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "folder_open.png"))

        try:
            image_painting = Image.open("icons/painting_decorated.png")
            self.image_painting = ImageTk.PhotoImage(image_painting)
        except FileNotFoundError:
            self.image_painting = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "painting_decorated.png"))

        try:
            image_paper_text = Image.open("icons/paper_text.png")
            self.image_paper_text = ImageTk.PhotoImage(image_paper_text)
        except FileNotFoundError:
            self.image_paper_text = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "paper_text.png"))

        try:
            image_paper_json = Image.open("icons/paper_json.png")
            self.image_paper_json = ImageTk.PhotoImage(image_paper_json)
        except FileNotFoundError:
            self.image_paper_json = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "paper_json.png"))

        try:
            image_paper_binary = Image.open("icons/paper_binary.png")
            self.image_paper_binary = ImageTk.PhotoImage(image_paper_binary)
        except FileNotFoundError:
            self.image_paper_binary = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "paper_binary.png"))

        try:
            image_paper_language = Image.open("icons/paper_language.png")
            self.image_paper_language = ImageTk.PhotoImage(image_paper_language)
        except FileNotFoundError:
            self.image_paper_language = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "paper_language.png"))

        try:
            image_cube = Image.open("icons/cube.png")
            self.image_cube = ImageTk.PhotoImage(image_cube)
        except FileNotFoundError:
            self.image_cube = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "cube.png"))

        try:
            image_fragment = Image.open("icons/fragment_shaded.png")
            self.image_fragment = ImageTk.PhotoImage(image_fragment)
        except FileNotFoundError:
            self.image_fragment = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "fragment_shaded.png"))

        try:
            image_vertex = Image.open("icons/vertex_shaded.png")
            self.image_vertex = ImageTk.PhotoImage(image_vertex)
        except FileNotFoundError:
            self.image_vertex = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "vertex_shaded.png"))

        try:
            image_nbt = Image.open("icons/nbt.png")
            self.image_nbt = ImageTk.PhotoImage(image_nbt)
        except FileNotFoundError:
            self.image_nbt = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "nbt.png"))

        try:
            image_exit = Image.open("icons/exit.png")
            self.image_exit = ImageTk.PhotoImage(image_exit)
        except FileNotFoundError:
            self.image_exit = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "exit.png"))

        try:
            image_refresh = Image.open("icons/reload_arrow.png")
            self.image_refresh = ImageTk.PhotoImage(image_refresh)
        except FileNotFoundError:
            self.image_refresh = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "reload_arrow.png"))

        try:
            image_chessboard = Image.open("icons/chessboard.png")
            self.image_chessboard = ImageTk.PhotoImage(image_chessboard)
        except FileNotFoundError:
            self.image_chessboard = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "chessboard.png"))

        try:
            image_grid = Image.open("icons/grid.png")
            self.image_grid = ImageTk.PhotoImage(image_grid)
        except FileNotFoundError:
            self.image_grid = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "grid.png"))

        try:
            image_zoom_in = Image.open("icons/zoom_in_shaded.png")
            self.image_zoom_in = ImageTk.PhotoImage(image_zoom_in)
        except FileNotFoundError:
            self.image_zoom_in = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "zoom_in_shaded.png"))

        try:
            image_zoom_out = Image.open("icons/zoom_out_shaded.png")
            self.image_zoom_out = ImageTk.PhotoImage(image_zoom_out)
        except FileNotFoundError:
            self.image_zoom_out = ""
            print("{} | FileNotFoundError: {}".format(datetime.now().strftime("%H:%M:%S"), "zoom_out_shaded.png"))
