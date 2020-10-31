/* Copyright (c) 2020 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.filetable.config

import com.deflatedpickle.quiver.filetable.util.FileLinkAction
import kotlinx.serialization.Serializable

@Serializable
data class FileTableSettings(
    var noFileLinkAction: FileLinkAction = FileLinkAction.REMOVE
)
