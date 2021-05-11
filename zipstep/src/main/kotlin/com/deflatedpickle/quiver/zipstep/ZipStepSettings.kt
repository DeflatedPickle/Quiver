/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.quiver.zipstep

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod

@Serializable
data class ZipStepSettings(
    @Required var compressionMethod: CompressionMethod = CompressionMethod.DEFLATE,
    @Required var compressionLevel: CompressionLevel = CompressionLevel.NORMAL,
    @Required var readHiddenFiles: Boolean = true,
    @Required var readHiddenFolders: Boolean = true,
    @Required var writeExtendedLocalFileHeader: Boolean = true,
    @Required var fileComment: String = "",
    @Required var symbolicLinkAction: ZipParameters.SymbolicLinkAction = ZipParameters.SymbolicLinkAction.INCLUDE_LINKED_FILE_ONLY,
    @Required var unixMode: Boolean = false
)
