package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.fabric.events.PlayerJoinCallback
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val LOGGER: Logger = LogUtils.getLogger()
    var playerList = HashMap<UUID, String>()

    override fun onInitialize() {
        ConfigManager.load()
        LOGGER.info("BCSS initialized")

        PlayerJoinCallback.EVENT.register(PlayerJoinCallback { _, serverPlayerEntity ->
            playerList[serverPlayerEntity.uuid] = serverPlayerEntity.displayName.string

            playerList.forEach { (uuid, name) ->
                debugMode("UUID: $uuid | Name: $name")
            }
        })
    }
}