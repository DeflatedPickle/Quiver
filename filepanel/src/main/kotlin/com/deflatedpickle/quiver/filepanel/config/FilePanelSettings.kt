/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.config

import com.deflatedpickle.quiver.filepanel.api.Program
import kotlinx.serialization.Contextual
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class FilePanelSettings(
    @Required val linkedPrograms: MutableList<@Contextual Program> = mutableListOf(),
)
