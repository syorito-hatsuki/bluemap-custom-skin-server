package dev.syoritohatsuki.bluemapcustomskinserver.integration

import net.minecraft.resources.Identifier

object IntegrationRegistry {
    private val integrations = mutableMapOf<Identifier, Integration>()

    fun register(integration: Integration) {
        integrations[integration.getIdentifier()] = integration
    }

    operator fun get(id: Identifier): Integration? = integrations[id]

    fun getIds(): Set<Identifier> = integrations.keys
}