package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.plugin.PlayerIconFactory
import de.bluecolored.bluemap.api.plugin.SkinProvider
import dev.syoritohatsuki.bluemapcustomskinserver.api.CustomApi
import dev.syoritohatsuki.bluemapcustomskinserver.api.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config.ServerType
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager.read
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val logger: Logger = LogUtils.getLogger()

    override fun onInitialize() {
        ConfigManager

        logger.info("BCSS initialized")

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            BlueMapAPI.onEnable { bluemap ->
                bluemap.plugin.skinProvider = SkinProvider { uuid ->
                    logger.debug("Config: {}", read())
                    Optional.ofNullable(
                        when (read().serverType) {
                            ServerType.CUSTOM -> CustomApi(
                                uuid, server.userCache?.getByUuid(uuid)?.get()?.name
                                    ?: throw RuntimeException(server.userCache?.getByUuid(uuid)?.get()?.name)
                            ).getSkin()

                            ServerType.MOJANG_LIKE -> MojangLikeApi(uuid).getSkin()
                        }.get()
                    )
                }

                if (read().directImage) bluemap.plugin.playerMarkerIconFactory = PlayerIconFactory { _, playerSkin ->
                    playerSkin
                }
            }
        }
    }
}
