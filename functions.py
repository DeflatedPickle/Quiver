#!/usr/bin/env python
# -*- coding: utf-8 -*-
""""""

import os
import zipfile

__title__ = "Functions"
__author__ = "DeflatedPickle"
__version__ = "1.0.0"


def folder_files(directory):
    count = 0

    for root, dirs, files in os.walk(directory):
        for name in dirs:
            count += 1

        for name in files:
            count += 1

    return count


def zip_files(file):
    with zipfile.ZipFile(file, "r") as z:
        return len(z.infolist())
