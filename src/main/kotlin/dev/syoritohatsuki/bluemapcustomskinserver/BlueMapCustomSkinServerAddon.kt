package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.plugin.PlayerIconFactory
import de.bluecolored.bluemap.api.plugin.SkinProvider
import dev.syoritohatsuki.bluemapcustomskinserver.api.CustomApi
import dev.syoritohatsuki.bluemapcustomskinserver.api.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.command.getAbstractPath
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config.ServerType
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager.read
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.command.CommandManager
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val logger: Logger = LogUtils.getLogger()

    override fun onInitialize() {
        ConfigManager

        logger.info("BCSS initialized")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(CommandManager.literal("bcss").getAbstractPath())
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            BlueMapAPI.onEnable { bluemap ->
                bluemap.plugin.skinProvider = SkinProvider { uuid ->
                    val name = server.apiServices.nameToIdCache.getByUuid(uuid)?.get()?.name

                    logger.debug("-----[ Skin Provider ]-----")
                    logger.debug("Config: {}", read())
                    logger.debug(name)
                    logger.debug(uuid.toString())
                    logger.debug("---------------------------")

                    Optional.ofNullable(
                        when (read().serverType) {
                            ServerType.CUSTOM -> CustomApi(
                                uuid, name ?: throw RuntimeException(name.toString())
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
