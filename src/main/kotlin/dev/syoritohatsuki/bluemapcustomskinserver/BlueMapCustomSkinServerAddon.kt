package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.plugin.SkinProvider
import dev.syoritohatsuki.bluemapcustomskinserver.api.CustomApi
import dev.syoritohatsuki.bluemapcustomskinserver.api.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config.ServerType
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager.read
import dev.syoritohatsuki.duckyupdater.DuckyUpdater
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val logger: Logger = LogUtils.getLogger()

    override fun onInitialize() {
        DuckyUpdater.checkForUpdate("yMAHcHNr", "bluemap_custom_skin_server")

        logger.info("BCSS initialized")

        ConfigManager

        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted { server ->

            BlueMapAPI.onEnable {

                it.plugin.skinProvider = SkinProvider { uuid ->
                    val username = server.playerManager.getPlayer(uuid)!!.entityName
                    Optional.ofNullable(
                        when (read().serverType) {
                            ServerType.CUSTOM -> CustomApi(uuid, username).getSkin()
                            ServerType.MOJANG_LIKE -> MojangLikeApi(uuid).getSkin()
                        }.get()
                    )
                }

            }

        })

    }

}