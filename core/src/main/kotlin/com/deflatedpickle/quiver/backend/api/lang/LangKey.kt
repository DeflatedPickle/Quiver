package com.deflatedpickle.quiver.backend.api.lang

data class LangKey(
    val lang: String,
    val line: Int,
    val key: String,
    val value: String,
)