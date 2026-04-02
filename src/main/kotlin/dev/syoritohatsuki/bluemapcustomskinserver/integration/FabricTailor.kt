package dev.syoritohatsuki.bluemapcustomskinserver.integration

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.ImageLoader
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.json.Json
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.storage.LevelResource
import org.samo_lego.fabrictailor.casts.TailoredPlayer
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import kotlin.io.path.toPath
import kotlin.jvm.optionals.getOrNull

object FabricTailor : Integration {

    private var server: MinecraftServer? = null

    override fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage?> =
        CompletableFuture.supplyAsync {
            if (!installed()) throw IllegalStateException("Fabric Tailor integration required Fabric Tailor mod [https://modrinth.com/mod/fabrictailor]")
            if (server == null) throw IllegalStateException("Server instance not provided")

            val skinFromPlayerData = runCatching {
                (server!!.playerList.getPlayer(uuid) as TailoredPlayer).fabrictailor_getSkinValue().getOrNull() ?: run {
                    logger.error("Tailor skin value not found in player data")
                    return@runCatching null
                }
            }.getOrNull()

            val skinFromCachedData = runCatching {
                NbtIo.readCompressed(server!!.getWorldPath(
                    LevelResource.PLAYER_DATA_DIR).resolve("$uuid.dat"),
                    NbtAccounter.unlimitedHeap()
                ).getCompound("fabrictailor:skin_data").getOrNull()?.getString("value")?.getOrNull()?: run {
                    logger.error("Invalid cached skin signature or it not exists")
                    return@runCatching null
                }
            }.getOrNull()

            val decodedSignature = String(Base64.getDecoder().decode(
                skinFromPlayerData ?: skinFromCachedData ?: run {
                    logger.error("Can't get skin from any on two sources")
                    return@supplyAsync null
                }
            ))

            val json = Json { ignoreUnknownKeys = true }

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

    fun installed(): Boolean = try {
        Class.forName("org.samo_lego.fabrictailor.FabricTailor")
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