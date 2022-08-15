package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.fabric.events.PlayerJoinCallback
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val LOGGER: Logger = LogUtils.getLogger()
    lateinit var PLAYER_LIST: HashMap<UUID, String>

    override fun onInitialize() {
        ConfigManager.load()

        PLAYER_LIST = HashMap<UUID, String>()

        PlayerJoinCallback.EVENT.register(PlayerJoinCallback { _, serverPlayerEntity ->
            PLAYER_LIST[serverPlayerEntity.uuid] = serverPlayerEntity.displayName.string
            PLAYER_LIST.forEach { (uuid, name) ->
                println("UUID: $uuid | Name: $name")
            }
        })

        LOGGER.info("BCSS initialized")
    }
}