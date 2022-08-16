package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val debug: Boolean = false,
    val serverType: ServerType = ServerType.MOJANG,
    val customSkinServerUrl: String = "https://localhost/",
    val custom: Custom = Custom()
) {
    @Serializable
    enum class ServerType {
        MOJANG, MOJANG_LIKE, CUSTOM
    }

    @Serializable
    data class Custom(
        val suffix: String = "",
        val getSkinBy: SkinBy = SkinBy.NAME,
        val skinByCase: SkinByCase = SkinByCase.LOWER
    ) {
        @Serializable
        enum class SkinBy {
            UUID, NAME
        }

        @Serializable
        enum class SkinByCase {
            LOWER, UPPER, DEFAULT
        }
    }
}
