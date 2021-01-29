package com.deflatedpickle.quiver.zipstep

import kotlinx.serialization.Serializable
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.CompressionMethod

@Serializable
data class ZipStepSettings(
    var compressionMethod: CompressionMethod = CompressionMethod.DEFLATE,
    var compressionLevel: CompressionLevel = CompressionLevel.NORMAL,
    var readHiddenFiles: Boolean = true,
    var readHiddenFolders: Boolean = true,
    var writeExtendedLocalFileHeader: Boolean = true,
    var fileComment: String = "",
    var symbolicLinkAction: ZipParameters.SymbolicLinkAction = ZipParameters.SymbolicLinkAction.INCLUDE_LINKED_FILE_ONLY,
    var unixMode: Boolean = false
)
