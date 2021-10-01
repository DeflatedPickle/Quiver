package com.deflatedpickle.quiver.backend.util.lang

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonObject
import blue.endless.jankson.JsonPrimitive
import com.deflatedpickle.quiver.backend.api.lang.LangReader
import java.io.File
import java.util.*

object JsonLangReader : LangReader {
    private val json = Jankson.builder().build()

    override fun load(file: File): Map<String, String> =
        json.load(file).mapValues { (it as JsonPrimitive).asString() }
}