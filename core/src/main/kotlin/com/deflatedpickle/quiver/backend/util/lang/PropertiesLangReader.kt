package com.deflatedpickle.quiver.backend.util.lang

import com.deflatedpickle.quiver.backend.api.lang.LangReader
import java.io.File
import java.util.*

object PropertiesLangReader : LangReader {
    override fun load(file: File): Map<String, String> {
        val values = mutableMapOf<String, String>()

        for (i in file.readLines()) {
            val split = i.split("=")
            values[split.first()] = split.last()
        }

        return values
    }
}