/* Copyright (c) 2021 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.quiver.packsquashstep

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class PackSquashStepSettings(
    /**
     * The location of your PackSquash executable
     */
    @Required var location: String = "tools/packsquash/",
    /**
     * The name of your PackSquash executable
     */
    @Required var executable: String = "packsquash",
    @Required var settings: String = "data/packsquash.toml"
)
