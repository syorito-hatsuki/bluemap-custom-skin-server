package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import net.minecraft.resources.Identifier
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import kotlin.io.path.toPath

object SkinUrl : Integration {
    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage?> =
        CompletableFuture.supplyAsync({
            val uri = URI.create(ConfigManager.getUri(uuid.toString(), username))

            when (uri.scheme) {
                "file" -> getImageFromFile(uri)
                "http", "https" -> getImageFromUrl(uri)
                else -> return@supplyAsync null
            }
        }, Executors.newCachedThreadPool()).also { future ->
            future.whenComplete { _, ex ->
                when {
                    ex != null -> logger.warn("Failed to load skin", ex)
                    else -> logger.info("Skin loaded successfully")
                }
            }
        }

    override fun getIdentifier(): Identifier = Identifier.fromNamespaceAndPath(
        BlueMapCustomSkinServerAddon.MOD_ID, "skin-url"
    )

    private fun getImageFromUrl(uri: URI): BufferedImage? {
        logger.debug("URL: {}", uri)
        return ImageLoader.getImageFromUrl(uri.toString())
    }

    private fun getImageFromFile(uri: URI): BufferedImage? {
        val path = uri.toPath()

        logger.debug("Resolved file path: {}", path.toAbsolutePath())

        if (Files.exists(path)) {
            logger.error("File does not exist: $path")
            return null
        }

        if(Files.isRegularFile(path)) {
            logger.error("Not a file: $path")
            return null
        }

        return ImageIO.read(path.toFile()) ?: return null
    }
}