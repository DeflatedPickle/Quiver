/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filepanel.api

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class Program(
    @Required val name: String,
    @Required val location: String,
    @Required val command: String,
    @Required val args: String,
    @Required val extensions: List<String>,
) /*{
    fun findPath() = BufferedReader(
        InputStreamReader(
            Runtime.getRuntime().exec(
                when (OSUtil.getOS()) {
                    OSUtil.OS.WINDOWS, OSUtil.OS.MAC -> "where"
                    OSUtil.OS.LINUX -> "which"
                    else -> throw RuntimeException()
                } + command
            ).inputStream
        )
    ).readLine()
}*/
