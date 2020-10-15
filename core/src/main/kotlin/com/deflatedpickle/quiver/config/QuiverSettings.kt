package com.deflatedpickle.quiver.config

import kotlinx.serialization.Serializable

@Serializable
data class QuiverSettings(
        var language: String = "en"
)
