package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val debug: Boolean = false,
    val serverType: ServerType = ServerType.CUSTOM,
    val url: String = "http://0.0.0.0/%uuid%"
) {
    @Serializable
    enum class ServerType {
        MOJANG_LIKE, CUSTOM
    }
}
