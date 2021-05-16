package com.deflatedpickle.quiver.textviewer

import com.deflatedpickle.quiver.textviewer.api.Theme
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class TextViewerSettings(
    @Required var theme: Theme = Theme("")
)