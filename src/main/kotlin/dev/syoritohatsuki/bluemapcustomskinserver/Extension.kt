package dev.syoritohatsuki.bluemapcustomskinserver

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager

fun debug(message: String) {
    if (ConfigManager.read().debug) logger.warn("DEBUG DATA: $message")
}