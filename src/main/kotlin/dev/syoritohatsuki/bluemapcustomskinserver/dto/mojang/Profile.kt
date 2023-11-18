package dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang


import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val properties: List<Property>
) {
    @Serializable
    data class Property(
        val name: String, val value: String
    )
}