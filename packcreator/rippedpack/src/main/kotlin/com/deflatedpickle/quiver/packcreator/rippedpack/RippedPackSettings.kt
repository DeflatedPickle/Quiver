/* Copyright (c) 2021 DeflatedPickle under the MIT license */

@file:Suppress("SpellCheckingInspection")

package com.deflatedpickle.quiver.packcreator.rippedpack

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class RippedPackSettings(
    /**
     * The location of your PackSquash executable
     */
    @Required var location: String = "tools/mcpackutil/",
    /**
     * The name of your PackSquash executable
     */
    @Required var executable: String = "mcpackutil",
)
