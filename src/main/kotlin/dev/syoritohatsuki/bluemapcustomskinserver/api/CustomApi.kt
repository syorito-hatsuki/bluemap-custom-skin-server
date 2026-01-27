package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {
    fun getSkin(): CompletableFuture<BufferedImage> = CompletableFuture.supplyAsync({
        val resolved = ConfigManager.read().url
            .replace("%uuid%", uuid.toString())
            .replace("%username%", name)

        val uri = URI.create(resolved)

        when (uri.scheme) {
            "file" -> getImageFromFile(uri)
            "http", "https" -> getImageFromUrl(uri)
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }, Executors.newCachedThreadPool()).also { future ->
        future.whenComplete { _, ex ->
            if (ex != null) {
                logger.warn("Failed to load skin", ex)
            } else {
                logger.info("Skin loaded successfully")
            }
        }
    }

    private fun getImageFromUrl(uri: URI): BufferedImage {
        logger.debug("URL: {}", uri)
        return ImageLoader.getImageFromUrl(uri.toString())
    }

    private fun getImageFromFile(uri: URI): BufferedImage {
        val path = Paths.get(uri)

        logger.debug("Resolved file path: {}", path.toAbsolutePath())

        require(Files.exists(path)) { "File does not exist: $path" }
        require(Files.isRegularFile(path)) { "Not a file: $path" }

        return ImageIO.read(path.toFile()) ?: throw IllegalStateException("Failed to read image: $path")
    }
}
