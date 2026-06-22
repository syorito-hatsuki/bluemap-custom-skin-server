package dev.syoritohatsuki.bluemapcustomskinserver.config

import dev.syoritohatsuki.bluemapcustomskinserver.config.serializer.IdentifierSerializer
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinUrl
import kotlinx.serialization.Serializable
import net.minecraft.resources.Identifier

@Serializable
data class ConfigV3(
    override val configVersion: Int = 3,
    @Serializable(with = IdentifierSerializer::class)
    val integration: Identifier = SkinUrl.getIdentifier(),
    val rawImage: Boolean = false,
    val uri: String = "",
): Config
