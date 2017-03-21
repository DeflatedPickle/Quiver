import tkinter as tk
from PIL import Image, ImageTk

class LoadImages:
    def __init__(self):
        icon = Image.open("quiver.ico")
        self.icon = ImageTk.PhotoImage(icon)

        image_folder_close = Image.open("icons/folder_close.png")
        self.image_folder_close = ImageTk.PhotoImage(image_folder_close)

        image_folder_open = Image.open("icons/folder_open.png")
        self.image_folder_open = ImageTk.PhotoImage(image_folder_open)

        image_painting = Image.open("icons/painting_decorated.png")
        self.image_painting = ImageTk.PhotoImage(image_painting)

        image_paper_text = Image.open("icons/paper_text.png")
        self.image_paper_text = ImageTk.PhotoImage(image_paper_text)

        image_paper_json = Image.open("icons/paper_json.png")
        self.image_paper_json = ImageTk.PhotoImage(image_paper_json)

        image_paper_binary = Image.open("icons/paper_binary.png")
        self.image_paper_binary = ImageTk.PhotoImage(image_paper_binary)

        image_paper_language = Image.open("icons/paper_language.png")
        self.image_paper_language = ImageTk.PhotoImage(image_paper_language)

        image_cube = Image.open("icons/cube.png")
        self.image_cube = ImageTk.PhotoImage(image_cube)

        image_fragment = Image.open("icons/fragment_shaded.png")
        self.image_fragment = ImageTk.PhotoImage(image_fragment)

        image_vertex = Image.open("icons/vertex_shaded.png")
        self.image_vertex = ImageTk.PhotoImage(image_vertex)

        image_nbt = Image.open("icons/nbt.png")
        self.image_nbt = ImageTk.PhotoImage(image_nbt)

        image_exit = Image.open("icons/exit.png")
        self.image_exit = ImageTk.PhotoImage(image_exit)

        image_refresh = Image.open("icons/reload_arrow.png")
        self.image_refresh = ImageTk.PhotoImage(image_refresh)

        image_find = Image.open("icons/exit.png")
        self.image_find = ImageTk.PhotoImage(image_find)
