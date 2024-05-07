package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val serverType: ServerType = ServerType.CUSTOM,
    val directImage: Boolean = false,
    val url: String = "http://0.0.0.0/%uuid%"
) {
    @Serializable
    enum class ServerType {
        MOJANG_LIKE, CUSTOM
    }
}
