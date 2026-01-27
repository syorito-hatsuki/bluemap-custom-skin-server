package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
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
import javax.imageio.ImageIO
import kotlin.io.path.toPath

object MojangLikeApi : Integration {

    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage> =
        CompletableFuture.supplyAsync {
            val json = Json { ignoreUnknownKeys = true }

            val uri = resolveUri(ConfigManager.getUri(uuid.toString(), username))
            BlueMapCustomSkinServerAddon.logger.debug("Profile source: {}", uri)

            val profileJson = when (uri.scheme) {
                "http", "https" -> readJsonFromHttp(uri)
                "file" -> readJsonFromFile(uri)
                else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
            }

            val texturesProperty = json.decodeFromString<Profile>(profileJson).properties.find {
                it.name == "textures"
            } ?: throw IllegalStateException("Missing textures property")

            val skinUrl = json.decodeFromString<TextureInfo>(
                String(Base64.getDecoder().decode(texturesProperty.value))
            ).textures.skin.url

            BlueMapCustomSkinServerAddon.logger.debug("Skin URL: $skinUrl")

            val skinUri = resolveUri(skinUrl)

            when (skinUri.scheme) {
                "http", "https" -> ImageLoader.getImageFromUrl(skinUrl)
                "file" -> ImageIO.read(skinUri.toPath().toFile())
                    ?: throw IllegalStateException("Failed to read image: $skinUri")

                else -> throw IllegalArgumentException("Unsupported URI scheme: ${skinUri.scheme}")
            }
        }.whenComplete { _, ex ->
            when {
                ex != null -> BlueMapCustomSkinServerAddon.logger.warn("Failed to load skin", ex)
                else -> BlueMapCustomSkinServerAddon.logger.info("Skin loaded successfully")
            }
        }

    private fun resolveUri(raw: String): URI = when {
        raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("file:") -> URI.create(raw)
        else -> Paths.get(raw).toUri()
    }

    private fun readJsonFromHttp(uri: URI): String = HttpClient.newHttpClient().send(
        HttpRequest.newBuilder(uri).header("User-Agent", ImageLoader.userAgent).build(),
        HttpResponse.BodyHandlers.ofString()
    ).body()

    private fun readJsonFromFile(uri: URI): String {
        val path = Paths.get(uri)
        BlueMapCustomSkinServerAddon.logger.debug("Reading JSON from file: {}", path.toAbsolutePath())

        require(Files.exists(path)) { "File does not exist: $path" }
        require(Files.isRegularFile(path)) { "Not a file: $path" }

        return Files.readString(path)
    }
}