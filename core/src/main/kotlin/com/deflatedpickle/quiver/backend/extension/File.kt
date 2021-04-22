/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.extension

import java.io.File

fun File.toAsset(): String {
    val folder = this.parentFile
    val namespace = folder.parentFile.parentFile

    return "${namespace.name}:${folder.name}/${this.nameWithoutExtension}"
}
