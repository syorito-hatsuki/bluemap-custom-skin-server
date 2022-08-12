package dev.syoritohatsuki.bluemapcustomskinserver

import com.mojang.logging.LogUtils
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.fabricmc.api.ModInitializer
import org.slf4j.Logger

object BlueMapCustomSkinServerAddon : ModInitializer {

    val LOGGER: Logger = LogUtils.getLogger()

    override fun onInitialize() {
        ConfigManager.load()
        LOGGER.info("BCSS initialized")
    }
}