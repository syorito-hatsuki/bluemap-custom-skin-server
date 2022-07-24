package dev.syoritohatsuki.bluemapcustomskinserver.config

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

object ConfigManager {
    private val json = Json { encodeDefaults = true; prettyPrint = true }
    private val configDir: File? = Paths.get("", "config", "bluemapcustomskinserver").toFile()
    private val configFile = File(configDir, "config.json")

    init {
        if (!configFile.exists()) {
            BlueMapCustomSkinServerAddon.LOGGER.info("Default config created")
            if (configDir?.exists() == false) configDir.mkdirs()
            configFile.apply {
                createNewFile()
                writeText(json.encodeToString(Config()))
            }
        }
    }

    fun read(): Config {
        BlueMapCustomSkinServerAddon.LOGGER.debug("Config readed")
        return json.decodeFromString(configFile.readText())
    }
}