package com.deflatedpickle.quiver.backend.util

enum class ExtraResourceType(
    val path: String
) {
    ICONS("icons"),
    MINECRAFT_ICONS("minecraft/icons"),
    MINECRAFT_LANG("minecraft/lang"),
    MINECRAFT_SOUNDS("minecraft/sounds"),
    REALMS_LANG("realms/lang"),
    REALMS_TEXTURES("realms/textures")
    ;

    override fun toString(): String = "assets/${name.toLowerCase().replace("_", "/")}"
}