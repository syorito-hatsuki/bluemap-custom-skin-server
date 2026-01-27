package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import kotlin.io.path.toPath

object SkinUrl : Integration {
    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage> = CompletableFuture.supplyAsync({
        val uri = URI.create(ConfigManager.getUri(uuid.toString(), username))

        when (uri.scheme) {
            "file" -> getImageFromFile(uri)
            "http", "https" -> getImageFromUrl(uri)
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }, Executors.newCachedThreadPool()).also { future ->
        future.whenComplete { _, ex ->
            when {
                ex != null -> logger.warn("Failed to load skin", ex)
                else -> logger.info("Skin loaded successfully")
            }
        }
    }

    private fun getImageFromUrl(uri: URI): BufferedImage {
        logger.debug("URL: {}", uri)
        return ImageLoader.getImageFromUrl(uri.toString())
    }

    private fun getImageFromFile(uri: URI): BufferedImage {
        val path = uri.toPath()

        logger.debug("Resolved file path: {}", path.toAbsolutePath())

        require(Files.exists(path)) { "File does not exist: $path" }
        require(Files.isRegularFile(path)) { "Not a file: $path" }

        return ImageIO.read(path.toFile()) ?: throw IllegalStateException("Failed to read image: $path")
    }
}