package com.deflatedpickle.quiver.backend.api

import java.util.*

abstract class Lang(
    prefix: String,
    lang: String? = null
) {
    private val locale = Locale(lang ?: Locale.getDefault().language)
    private val bundle = ResourceBundle.getBundle(
        "lang/${prefix}",
        locale
    )

    /**
     * Translate a key from this bundle
     */
    @Suppress("unused")
    fun trans/*rights*/(key: String): String = this.bundle.getString(key)
}