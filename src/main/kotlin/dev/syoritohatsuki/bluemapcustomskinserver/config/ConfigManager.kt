package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

object ConfigManager {
    private val configDir: File = Paths.get("", "config", "bluemapcustomskinserver").toFile()
    private val configFile = File(configDir, "config.json")

    private val configJson = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    init {
        if (!configDir.exists()) configDir.mkdirs()
        if (!configFile.exists()) configFile.writeText(configJson.encodeToString(Config()))
    }

    fun read(): Config = configJson.decodeFromString(configFile.readText())
}