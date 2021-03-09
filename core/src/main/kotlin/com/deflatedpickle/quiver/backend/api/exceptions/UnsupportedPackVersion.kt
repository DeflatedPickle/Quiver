/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.backend.api.exceptions

class UnsupportedPackVersion(
    version: Int
) : Exception(
    "$version is not a supported pack version for this operation"
)
