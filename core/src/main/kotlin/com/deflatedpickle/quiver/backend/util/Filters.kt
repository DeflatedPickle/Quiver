/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.util

import so.madprogrammer.PatternFilter

object Filters {
    val FILE = PatternFilter("""[^\\./:*?\"<>|]*""")
    val PATH_WINDOWS = PatternFilter("""[A-Za-z]:[^:*?"<>|]*""")
    // https://stackoverflow.com/a/28989719
    val PATH_UNIX = PatternFilter("""^(/[^/ \n]*)+/?$""")
}
