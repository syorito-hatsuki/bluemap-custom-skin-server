package dev.syoritohatsuki.bluemapcustomskinserver

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.LOGGER
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager

fun debugMode(message: String) {
    if (ConfigManager.read().debug) {
        LOGGER.info("DEBUG DATA: $message")
    }
}