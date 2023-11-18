package dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextureInfo(
    @SerialName("textures") val textures: Textures
) {
    @Serializable
    data class Textures(
        @SerialName("SKIN") val skin: Skin
    ) {
        @Serializable
        data class Skin(
            @SerialName("url") val url: String
        )
    }
}