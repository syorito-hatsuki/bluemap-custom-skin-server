package dev.syoritohatsuki.bluemapcustomskinserver

import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import org.slf4j.Logger

fun Logger.debugMessage(message: String) {
    if (ConfigManager.read().debug) warn("\u001B[33mDebug: $message")
}