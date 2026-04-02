package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class ConfigV2(
    override val configVersion: Int = 2,
    val integration: Integration = Integration.SKIN_URL,
    val rawImage: Boolean = false,
    val uri: String = "",
): Config {
    enum class Integration {
        MOJANG_LIKE_API,
        SKIN_URL,
        SKIN_RESTORER,
        FABRIC_TAILOR
    }
}
