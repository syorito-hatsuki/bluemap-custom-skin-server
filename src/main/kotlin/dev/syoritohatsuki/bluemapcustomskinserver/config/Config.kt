package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val serverType: ServerType = ServerType.MOJANG,
    val customSkinServerUrl: String = "https://localhost/",
    val getSkinBy: SkinBy = SkinBy.UUID,
    val suffix: String = "",
    val skinByCase: SkinByCase = SkinByCase.DEFAULT
) {
    @Serializable
    enum class SkinByCase {
        LOWER, UPPER, DEFAULT
    }

    @Serializable
    enum class ServerType {
        MOJANG, MOJANG_LIKE, CUSTOM
    }

    @Serializable
    enum class SkinBy {
        UUID, NAME
    }
}
