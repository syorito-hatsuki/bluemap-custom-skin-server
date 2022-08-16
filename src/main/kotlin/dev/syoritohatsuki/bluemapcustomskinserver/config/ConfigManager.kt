package dev.syoritohatsuki.bluemapcustomskinserver.config

import dev.syoritohatsuki.bluemapcustomskinserver.debugMode
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

object ConfigManager {
    private val json = Json { encodeDefaults = true; prettyPrint = true; ignoreUnknownKeys = true }
    private val configDir: File = Paths.get("", "config", "bluemapcustomskinserver").toFile()
    private val configFile = File(configDir, "config.json")

    init {
        if (!configFile.exists()) {
            if (!configDir.exists()) {
                configDir.mkdirs()
                debugMode("Default config folder created")
            }
            configFile.apply {
                createNewFile()
                debugMode("Default config file created")
                writeText(json.encodeToString(Config()))
            }
        }
    }

    fun load() = configFile.writeText(json.encodeToString(read()))

    fun read(): Config {
        return json.decodeFromString(configFile.readText())
    }
}