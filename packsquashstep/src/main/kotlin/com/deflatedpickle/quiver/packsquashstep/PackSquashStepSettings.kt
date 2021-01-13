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
    val arguments: List<String> = listOf(
        "-c", // This compresses every thing that's already compressed, like PNGs
        "-a", // This makes PackSquash tell us what files it skips
        "-m", // This includes all mod files
        "-o" // This obfuscates the ZIP meaning some programs can't read it
    ),
    val timeout: Long = 5L // The amount of seconds to wait if this command doesn't start
)
