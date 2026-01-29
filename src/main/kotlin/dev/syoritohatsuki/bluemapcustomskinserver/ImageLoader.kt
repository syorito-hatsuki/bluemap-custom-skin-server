package dev.syoritohatsuki.bluemapcustomskinserver

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import net.fabricmc.loader.api.FabricLoader
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO
import kotlin.jvm.optionals.getOrNull

object ImageLoader {
    private val modVersion = FabricLoader.getInstance().getModContainer("bluemap_custom_skin_server")
        .getOrNull()?.metadata?.version?.friendlyString ?: "version not found"
    val userAgent =
        "syorito-hatsuki/bluemap-custom-skin-server/${modVersion} (https://github.com/syorito-hatsuki/bluemap-custom-skin-server/issues)"

    fun getImageFromUrl(url: String): BufferedImage? {
        try {
            val connection = URI(url).toURL().openConnection()
            connection.setRequestProperty("User-Agent", userAgent)
            return connection.getInputStream().use(ImageIO::read)
        } catch (_: Exception) {
            logger.error("Failed to load image: $url")
            return null
        }
    }
}
