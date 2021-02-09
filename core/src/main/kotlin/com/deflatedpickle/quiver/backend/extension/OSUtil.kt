/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.extension

import com.deflatedpickle.marvin.util.OSUtil
import com.deflatedpickle.quiver.backend.util.Filters
import so.madprogrammer.PatternFilter

fun OSUtil.OS.toPatternFilter(): PatternFilter = if (this == OSUtil.OS.WINDOWS) {
    Filters.PATH_WINDOWS
} else {
    Filters.PATH_UNIX
}
