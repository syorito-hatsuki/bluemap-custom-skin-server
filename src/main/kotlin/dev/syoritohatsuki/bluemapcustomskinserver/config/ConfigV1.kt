package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class ConfigV1(
    override val configVersion: Int = 1,
    val serverType: ServerType = ServerType.CUSTOM,
    val directImage: Boolean = false,
    val url: String = "http://0.0.0.0/%uuid%"
): Config {
    @Serializable
    enum class ServerType {
        MOJANG_LIKE,
        CUSTOM
    }
}