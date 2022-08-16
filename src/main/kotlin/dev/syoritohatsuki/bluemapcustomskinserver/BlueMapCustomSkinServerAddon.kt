package dev.syoritohatsuki.bluemapcustomskinserver

import de.bluecolored.bluemap.fabric.events.PlayerJoinCallback
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.fabricmc.api.ModInitializer
import java.util.*
import java.util.logging.Logger

object BlueMapCustomSkinServerAddon : ModInitializer {

    val LOGGER: Logger = Logger.getLogger(javaClass.simpleName)
    var playerList = HashMap<UUID, String>()

    override fun onInitialize() {
        ConfigManager
        LOGGER.info("BCSS initialized")

        PlayerJoinCallback.EVENT.register(PlayerJoinCallback { _, serverPlayerEntity ->
            playerList[serverPlayerEntity.uuid] = serverPlayerEntity.displayName.string

            playerList.forEach { (uuid, name) ->
                debugMode("UUID: $uuid | Name: $name")
            }
        })
    }
}