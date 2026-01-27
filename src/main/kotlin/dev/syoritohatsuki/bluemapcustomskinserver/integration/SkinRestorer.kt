package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.json.Json
import net.lionarius.skinrestorer.SkinRestorer
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.io.path.toPath

object SkinRestorer : Integration {
    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage> =
        CompletableFuture.supplyAsync {
            if (!installed()) throw IllegalStateException("Skin Restorer integration required Skin Restorer mod [https://modrinth.com/mod/skinrestorer]")

            val json = Json { ignoreUnknownKeys = true }

            val decodedSignature = String(
                Base64.getDecoder().decode(
                    SkinRestorer.getSkinStorage().getSkin(uuid).value?.value
                        ?: throw IllegalStateException("Invalid skin signature or it not exists")
                )
            )

            val skinUrl = json.decodeFromString<TextureInfo>(decodedSignature).textures.skin.url

            logger.debug("Skin URL: $skinUrl")

            val skinUri = resolveUri(skinUrl)

            when (skinUri.scheme) {
                "http", "https" -> ImageLoader.getImageFromUrl(skinUrl)
                "file" -> ImageIO.read(skinUri.toPath().toFile())
                    ?: throw IllegalStateException("Failed to read image: $skinUri")

                else -> throw IllegalArgumentException("Unsupported URI scheme: ${skinUri.scheme}")
            }
        }.whenComplete { _, ex ->
            when {
                ex != null -> logger.warn("Failed to load skin", ex)
                else -> logger.info("Skin loaded successfully")
            }
        }

    fun installed(): Boolean = try {
        Class.forName("net.lionarius.skinrestorer.SkinRestorer")
        true
    } catch (_: ClassNotFoundException) {
        false
    }

    private fun resolveUri(raw: String): URI = when {
        raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("file:") -> URI.create(raw)
        else -> Paths.get(raw).toUri()
    }
}