/* Copyright (c) 2021 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.quiver.translationmatrix.config

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class TranslationMatrixSettings(
    // Random languages as default that might be popular
    // Like all settings, these can be changed via your config file
    // But if more defaults are wanted, open an issue
    @Required val langs: MutableList<String> = mutableListOf(
        // "en_gb",
        "en_us",
        // "en_pt",
        // "en_ud",
        "de_de",
        "es_es",
        "fr_fr",
        "ja_jp",
        "zh_cn",
    )
)
