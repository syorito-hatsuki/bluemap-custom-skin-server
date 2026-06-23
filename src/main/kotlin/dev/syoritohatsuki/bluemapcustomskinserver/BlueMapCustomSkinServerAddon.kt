package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.plugin.PlayerIconFactory
import de.bluecolored.bluemap.api.plugin.SkinProvider
import dev.faststats.ErrorTracker
import dev.faststats.Metrics
import dev.faststats.fabric.FabricContext
import dev.syoritohatsuki.bluemapcustomskinserver.command.getAbstractPath
import dev.syoritohatsuki.bluemapcustomskinserver.command.getAvailableIntegrations
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager.read
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.register
import dev.syoritohatsuki.bluemapcustomskinserver.dsl.rootLiteral
import dev.syoritohatsuki.bluemapcustomskinserver.integration.IntegrationRegistry
import dev.syoritohatsuki.bluemapcustomskinserver.integration.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinRestorer
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinUrl
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.commands.Commands
import org.slf4j.Logger
import java.util.*

object BlueMapCustomSkinServerAddon : ModInitializer {
    const val MOD_ID = "bluemap-custom-skin-server"
    val ERROR_TRACKER: ErrorTracker = ErrorTracker.contextAware()

    val logger: Logger = LogUtils.getLogger()

    private val context: FabricContext =
        FabricContext.Factory(MOD_ID, "9644e9ed2bde89e5044ac0f5e661267d")
            .metrics(Metrics.Factory::create)
            .errorTrackerService(ERROR_TRACKER)
            .create()

    override fun onInitialize() {

        logger.info("BCSS initialized")

        IntegrationRegistry.register(MojangLikeApi)
        IntegrationRegistry.register(SkinRestorer)
        IntegrationRegistry.register(SkinUrl)

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register {
                rootLiteral("bcss") {
                    getAbstractPath("gap", "get-abstract-path")
                    getAvailableIntegrations("gai", "get-available-integrations")
                }.requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            BlueMapAPI.onEnable { bluemap ->
                logger.info("Registered ${IntegrationRegistry.getIds().size} integrations for Custom Skin Server")

                IntegrationRegistry.getIds().forEach { id ->
                    logger.info("- $id")
                }

                logger.info("")

                bluemap.plugin.skinProvider = SkinProvider { uuid ->
                    val username = server.services().nameToIdCache.get(uuid).get().name

                    logger.debug("-----[ Skin Provider ]-----")
                    logger.debug("Config: {}", read())
                    logger.debug(username)
                    logger.debug(uuid.toString())
                    logger.debug("---------------------------")

                    Optional.ofNullable(
                        try {
                            val integration = IntegrationRegistry[read().integration]
                                ?: error("Unknown integration '${read().integration}'")

                            integration.getSkin(uuid, username).get()
                        } catch (_: Exception) {
                            // Just to avoid hidden throw's
                            null
                        }
                    )
                }

                if (read().rawImage) bluemap.plugin.playerMarkerIconFactory = PlayerIconFactory { _, playerSkin ->
                    playerSkin
                }
            }
        }
    }
}
