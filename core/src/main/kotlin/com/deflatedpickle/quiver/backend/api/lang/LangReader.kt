@file:Suppress("DEPRECATION", "SpellCheckingInspection")

package com.deflatedpickle.quiver.backend.api.lang

import com.deflatedpickle.haruhi.api.Registry
import com.deflatedpickle.quiver.Quiver
import com.deflatedpickle.quiver.backend.util.lang.JsonLangReader
import com.deflatedpickle.quiver.backend.util.lang.PropertiesLangReader
import java.io.File
import java.util.*

interface LangReader {
    companion object {
        val registry = Registry<String, LangReader>()

        private fun loopLangs(langs: List<String>, run: (File, String) -> Unit) {
            // There could be mods with their own keys
            Quiver.packDirectory?.resolve("assets")?.listFiles()?.forEach { f ->
                f.resolve("lang").listFiles()?.forEach { lf ->
                    val name = lf.nameWithoutExtension.toLowerCase()
                    if (name in langs) {
                        run(lf, name)
                    }
                }
            }
        }

        fun read(langs: List<String>, handler: (String, String) -> Unit) {
            loopLangs(langs) { f, _ ->
                when (Quiver.format) {
                    0, 1, 2, 3 -> PropertiesLangReader.read(f, handler)
                    else -> JsonLangReader.read(f, handler)
                }
            }
        }
    }

    fun load(file: File): Map<String, String>
    fun read(file: File, handler: (String, String) -> Unit): Map<String, String> {
        val new = load(file)

        for ((k, v) in new) {
            handler(k, v)
        }

        return new
    }
}