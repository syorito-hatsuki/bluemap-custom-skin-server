package dev.syoritohatsuki.bluemapcustomskinserver.config

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import dev.syoritohatsuki.bluemapcustomskinserver.integration.MojangLikeApi
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinRestorer
import dev.syoritohatsuki.bluemapcustomskinserver.integration.SkinUrl
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.minecraft.resources.Identifier
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
            !configFile.exists() -> configFile.writeText(configJson.encodeToString(ConfigV3()))
            else -> configFile.writeText(configJson.encodeToString(migrateConfig()))
        }
    }

    fun read(): ConfigV3 = configJson.decodeFromString(configFile.readText())

    fun getUri(uuid: String, username: String): String = read().uri
        .replace("%uuid%", uuid)
        .replace("%username%", username)

    fun migrateConfig(): ConfigV3 {
        val json = configFile.readText()
        val element = Json.parseToJsonElement(json)
        val version = element.jsonObject["configVersion"]?.jsonPrimitive?.int ?: 1

        return when (version) {
            1 -> configJson.decodeFromString<ConfigV1>(json).toV2().toV3()
            2 -> configJson.decodeFromString<ConfigV2>(json).toV3()
            3 -> configJson.decodeFromString<ConfigV3>(json)
            else -> error("Unsupported config version: $version")
        }
    }

    private fun ConfigV1.toV2(): ConfigV2 = ConfigV2(
        integration = when (this.serverType) {
            ConfigV1.ServerType.CUSTOM -> ConfigV2.Integration.SKIN_URL
            ConfigV1.ServerType.MOJANG_LIKE -> ConfigV2.Integration.MOJANG_LIKE_API
        }, rawImage = this.directImage, uri = this.url
    )

    private fun ConfigV2.toV3(): ConfigV3 = ConfigV3(
        integration = when (this.integration) {
            ConfigV2.Integration.SKIN_URL -> SkinUrl.getIdentifier()
            ConfigV2.Integration.MOJANG_LIKE_API -> MojangLikeApi.getIdentifier()
            ConfigV2.Integration.FABRIC_TAILOR -> run {
                BlueMapCustomSkinServerAddon.logger.warn("Fabric Tailor is now standalone module, please install it manually:")
                BlueMapCustomSkinServerAddon.logger.warn("- CurseForge: https://www.curseforge.com/minecraft/mc-mods/bluemap-custom-skin-server-fabric-tailor-integration")
                BlueMapCustomSkinServerAddon.logger.warn("- Modrinth: https://modrinth.com/project/bluemap-custom-skin-server-fabric-tailor-integration")

                return@run Identifier.fromNamespaceAndPath(
                    "bluemap-custom-skin-server-fabric-tailor-integration", "fabric-tailor"
                )
            }

            ConfigV2.Integration.SKIN_RESTORER -> SkinRestorer.getIdentifier()
        }, rawImage = this.rawImage, uri = this.uri
    )
}