package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.json.Json
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.skinsrestorer.api.SkinsRestorerProvider
import net.skinsrestorer.api.property.MojangSkinDataResult
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.io.path.toPath
import kotlin.jvm.optionals.getOrNull

object SkinsRestorer : Integration {
    val json = Json { ignoreUnknownKeys = true }
    private var server: MinecraftServer? = null

    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage?> =
        CompletableFuture.supplyAsync {
            if (!SkinRestorer.isDependenciesInstalled()) throw IllegalStateException("Skins Restorer integration required Skins Restorer mod [https://modrinth.com/mod/skinsrestorer]")

            val skinData: MojangSkinDataResult =
                SkinsRestorerProvider.get().skinStorage.getPlayerSkin(uuid.toString(), false).getOrNull()
                    ?: return@supplyAsync null


            val skinUrl = json.decodeFromString<TextureInfo>(
                String(Base64.getDecoder().decode(skinData.skinProperty.value))
            ).textures.skin.url

            logger.debug("Skin URL: $skinUrl")

            val skinUri = resolveUri(skinUrl)

            when (skinUri.scheme) {
                "http", "https" -> ImageLoader.getImageFromUrl(skinUrl)
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
        BlueMapCustomSkinServerAddon.MOD_ID, "skins-restorer"
    )

    override fun isDependenciesInstalled(): Boolean = try {
        Class.forName("net.skinsrestorer.mod.fabric.SRFabricMod")
        true
    } catch (_: ClassNotFoundException) {
        false
    }

    fun provideServer(server: MinecraftServer) {
        if (this.server != null) return
        this.server = server
    }

    private fun resolveUri(raw: String): URI = when {
        raw.startsWith("http://") || raw.startsWith("https://") || raw.startsWith("file:") -> URI.create(raw)
        else -> Paths.get(raw).toUri()
    }
}