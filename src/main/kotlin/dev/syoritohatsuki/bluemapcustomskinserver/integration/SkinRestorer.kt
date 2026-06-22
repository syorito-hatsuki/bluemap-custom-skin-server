package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.json.Json
import net.lionarius.skinrestorer.SkinRestorer
import net.minecraft.resources.Identifier
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.io.path.toPath

object SkinRestorer : Integration {
    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage?> =
        CompletableFuture.supplyAsync {
            if (!isDependenciesInstalled()) throw IllegalStateException("Skin Restorer integration required Skin Restorer mod [https://modrinth.com/mod/skinrestorer]")

            val skinValue = SkinRestorer.getSkinStorage().getSkin(uuid).value?.value ?: run {
                logger.error("Invalid skin signature or it not exists")
                return@supplyAsync null
            }

            val json = Json { ignoreUnknownKeys = true }

            val decodedSignature = String(Base64.getDecoder().decode(skinValue))

            val skinUrl = json.decodeFromString<TextureInfo>(decodedSignature).textures.skin.url

            logger.debug("Skin URL: $skinUrl")

            val skinUri = resolveUri(skinUrl)

            when (skinUri.scheme) {
                "http", "https" -> return@supplyAsync ImageLoader.getImageFromUrl(skinUrl)
                "file" -> ImageIO.read(skinUri.toPath().toFile()) ?: run {
                    logger.error("Can't get image from file: $skinUrl")
                    return@supplyAsync null
                }

                else -> {
                    logger.error("Unsupported URI scheme: ${skinUri.scheme}")
                    return@supplyAsync null
                }
            }
        }.whenComplete { _, ex ->
            when {
                ex != null -> logger.warn("Failed to load skin", ex)
                else -> logger.info("Skin loaded successfully")
            }
        }

    override fun getIdentifier(): Identifier = Identifier.fromNamespaceAndPath(
        BlueMapCustomSkinServerAddon.MOD_ID, "skin-restorer"
    )

    override fun isDependenciesInstalled(): Boolean = try {
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