package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.fabric.events.PlayerJoinCallback
import de.bluecolored.bluemap.fabric.events.PlayerLeaveCallback
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val LOGGER: Logger = LogUtils.getLogger()
    val PLAYER_LIST = HashMap<UUID, String>()

    override fun onInitialize() {
        ConfigManager.load()
        PlayerJoinCallback.EVENT.register(PlayerJoinCallback { _, serverPlayerEntity ->
            LOGGER.info("Added ${serverPlayerEntity.displayName.string} to list ")
            PLAYER_LIST[serverPlayerEntity.uuid] = serverPlayerEntity.displayName.string
        })
        PlayerLeaveCallback.EVENT.register(PlayerLeaveCallback { _, serverPlayerEntity ->
            LOGGER.info("Removed ${serverPlayerEntity.displayName.string} from list ")
            PLAYER_LIST.remove(serverPlayerEntity.uuid)
        })

        LOGGER.info("BCSS initialized")
    }
}