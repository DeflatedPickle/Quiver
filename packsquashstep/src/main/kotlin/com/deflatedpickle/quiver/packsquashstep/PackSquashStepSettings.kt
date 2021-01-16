package com.deflatedpickle.quiver.packsquashstep

import kotlinx.serialization.Serializable

@Serializable
data class PackSquashStepSettings(
    /**
     * The location of your PackSquash executable
     */
    var location: String = "../natives",
    /**
     * The name of your PackSquash executable
     */
    @Suppress("SpellCheckingInspection")
    var executable: String = "packsquash",
    /**
     * The amount of seconds to wait if this command doesn't start
     */
    val timeout: Long = 5L
)
