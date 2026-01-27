package dev.syoritohatsuki.bluemapcustomskinserver.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
        when {
            !configFile.exists() -> configFile.writeText(configJson.encodeToString(ConfigV2()))
            else -> configFile.writeText(configJson.encodeToString(migrateConfig()))
        }
    }

    fun read(): ConfigV2 = configJson.decodeFromString(configFile.readText())

    fun getUri(uuid: String, username: String): String = read().uri
        .replace("%uuid%", uuid)
        .replace("%username%", username)

    fun migrateConfig(): ConfigV2 {
        val json = configFile.readText()
        val element = Json.parseToJsonElement(json)
        val version = element.jsonObject["configVersion"]?.jsonPrimitive?.int ?: 1

        return when (version) {
            1 -> configJson.decodeFromString<ConfigV1>(json).toV2()
            2 -> configJson.decodeFromString<ConfigV2>(json)
            else -> error("Unsupported config version: $version")
        }
    }

    private fun ConfigV1.toV2(): ConfigV2 = ConfigV2(
        integration = when (this.serverType) {
            ConfigV1.ServerType.CUSTOM -> ConfigV2.Integration.SKIN_URL
            ConfigV1.ServerType.MOJANG_LIKE -> ConfigV2.Integration.MOJANG_LIKE_API
        }, rawImage = this.directImage, uri = this.url
    )
}