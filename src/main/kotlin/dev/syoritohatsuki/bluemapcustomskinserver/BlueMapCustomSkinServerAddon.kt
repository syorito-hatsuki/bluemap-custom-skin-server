package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.plugin.PlayerIconFactory
import de.bluecolored.bluemap.api.plugin.SkinProvider
import dev.syoritohatsuki.bluemapcustomskinserver.command.getAbstractPath
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager.read
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigV2.Integration
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.register
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.rootLiteral
import dev.syoritohatsuki.bluemapcustomskinserver.integration.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinRestorer
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinUrl
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.ADMINS_CHECK
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {

    val logger: Logger = LogUtils.getLogger()

    override fun onInitialize() {
        ConfigManager

        logger.info("BCSS initialized")

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register {
                rootLiteral("bcss") {
                    getAbstractPath("gap", "get-abstract-path")
                }.requires(CommandManager.requirePermissionLevel(ADMINS_CHECK))
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            BlueMapAPI.onEnable { bluemap ->
                bluemap.plugin.skinProvider = SkinProvider { uuid ->
                    val username = server.apiServices.nameToIdCache.getByUuid(uuid)?.get()?.name.let {
                        it ?: run {
                            logger.error("Can't get username from UUID $uuid")
                            logger.error("Technically impossible cause all players have both")
                            return@SkinProvider Optional.ofNullable(null)
                        }
                    }

                    logger.debug("-----[ Skin Provider ]-----")
                    logger.debug("Config: {}", read())
                    logger.debug(username)
                    logger.debug(uuid.toString())
                    logger.debug("---------------------------")

                    Optional.ofNullable(try {
                        when (read().integration) {
                            Integration.SKIN_URL -> SkinUrl.getSkin(uuid, username)
                            Integration.MOJANG_LIKE_API -> MojangLikeApi.getSkin(uuid, username)
                            Integration.SKIN_RESTORER -> SkinRestorer.getSkin(uuid, username)
                        }.get()
                    } catch (_: Exception) {
                        // Just to avoid hidden throw's
                        null
                    })
                }

                if (read().rawImage) bluemap.plugin.playerMarkerIconFactory = PlayerIconFactory { _, playerSkin ->
                    playerSkin
                }
            }
        }
    }
}
