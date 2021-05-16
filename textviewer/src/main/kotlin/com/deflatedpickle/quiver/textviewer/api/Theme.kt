package com.deflatedpickle.quiver.textviewer.api

import com.deflatedpickle.quiver.textviewer.util.ThemeSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable(with = ThemeSerializer::class)
class Theme(
    val id: String
)