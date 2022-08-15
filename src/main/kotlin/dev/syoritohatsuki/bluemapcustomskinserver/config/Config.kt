package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
   val serverType: ServerType = ServerType.MOJANG,
   val customSkinServerUrl: String = "https://localhost/",
   val getSkinBy: SkinBy = SkinBy.UUID
)

@Serializable
enum class ServerType {
   MOJANG, MOJANG_LIKE, CUSTOM
}

@Serializable
enum class SkinBy {
   UUID, NAME
}