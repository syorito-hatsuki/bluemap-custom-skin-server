package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.Profile
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture

class MojangLikeApi(private val uuid: UUID) {

    fun getSkin(): CompletableFuture<BufferedImage> = CompletableFuture.supplyAsync {
        val json = Json { ignoreUnknownKeys = true }

        val resolved = ConfigManager.read().url.replace("%uuid%", uuid.toString())

        val uri = resolveUri(resolved)
        logger.debug("Profile source: {}", uri)

        val profileJson = when (uri.scheme) {
            "http", "https" -> readJsonFromHttp(uri)
            "file" -> readJsonFromFile(uri)
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }

        val profile = json.decodeFromString<Profile>(profileJson)

        val texturesProperty = profile.properties.find { it.name == "textures" }
            ?: throw IllegalStateException("Missing textures property")

        val textureInfo = json.decodeFromString<TextureInfo>(
            String(Base64.getDecoder().decode(texturesProperty.value))
        )

        val skinUrl = textureInfo.textures.skin.url
        logger.debug("Skin URL: $skinUrl")

        ImageLoader.getImageFromUrl(skinUrl)
    }.whenComplete { _, ex ->
        if (ex != null) {
            logger.warn("Failed to load skin", ex)
        } else {
            logger.info("Skin loaded successfully")
        }
    }

    private fun resolveUri(raw: String): URI = when {
        raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("file:") -> {
            URI.create(raw)
        }

        else -> {
            Paths.get(raw).toUri()
        }
    }

    private fun readJsonFromHttp(uri: URI): String = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(uri).header("User-Agent", ImageLoader.userAgent).build(),
            HttpResponse.BodyHandlers.ofString()
        ).body()

    private fun readJsonFromFile(uri: URI): String {
        val path = Paths.get(uri)
        logger.debug("Reading JSON from file: {}", path.toAbsolutePath())

        require(Files.exists(path)) { "File does not exist: $path" }
        require(Files.isRegularFile(path)) { "Not a file: $path" }

        return Files.readString(path)
    }
}