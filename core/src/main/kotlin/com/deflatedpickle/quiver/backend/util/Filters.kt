package com.deflatedpickle.quiver.backend.util

import so.madprogrammer.PatternFilter

object Filters {
    val FILE = PatternFilter("[^\\./:*?\"<>|]*")
    val PATH = PatternFilter("[A-Za-z]:*[^./:*?\"<>|]*")
}