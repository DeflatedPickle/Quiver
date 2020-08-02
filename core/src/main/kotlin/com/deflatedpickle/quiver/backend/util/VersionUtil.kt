package com.deflatedpickle.quiver.backend.util

object VersionUtil {
    val RELEASE = Regex("""\d+\.\d+(\.\d+)?""")
    val ALPHA = Regex("""\d+w\d+a""")
    val BETA = Regex("""\d+w\d+b""")
}