package com.deflatedpickle.quiver.packsquashstep

import kotlinx.serialization.Serializable

@Serializable
data class PackSquashStepSettings(
    /**
     * The location of your PackSquash executable
     */
    var location: String = "tools/packsquash/",
    /**
     * The name of your PackSquash executable
     */
    @Suppress("SpellCheckingInspection")
    var executable: String = "packsquash",
    var settings: String = "data/packsquash.toml"
)
