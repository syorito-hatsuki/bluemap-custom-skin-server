package dev.syoritohatsuki.bluemapcustomskinserver.config

@kotlinx.serialization.Serializable
data class Config(
   val enableCustomServer: Boolean = true,
   val customSkinServerUrl: String = "https://localhost/"
)