package com.deflatedpickle.quiver.textviewer.util

import com.deflatedpickle.quiver.textviewer.api.Theme
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@ExperimentalSerializationApi
@Serializer(forClass = Theme::class)
object ThemeSerializer : KSerializer<Theme> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            serialName = "Theme",
            kind = PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: Theme) =
        encoder.encodeString(value.id)

    override fun deserialize(decoder: Decoder): Theme =
        Theme(decoder.decodeString())
}