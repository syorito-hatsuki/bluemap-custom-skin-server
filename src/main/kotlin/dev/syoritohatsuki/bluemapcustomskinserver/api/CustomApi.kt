package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {
    fun getSkin(): CompletableFuture<BufferedImage> = CompletableFuture<BufferedImage>().apply {
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                val config = ConfigManager.read()
                config.let {
                    logger.debug(it.url)
                    logger.debug(it.serverType.name)
                }
                logger.debug(name)
                logger.debug(uuid.toString())
                ConfigManager.read().url.replace("%uuid%", uuid.toString()).replace("%username%", name).let {
                    logger.debug(it)
                    complete(ImageIO.read(URI(it).toURL().openStream()))
                }
            }.onSuccess {
                logger.info("Skin loaded: $it")
            }.onFailure {
                logger.warn(it.message)
            }
        }
    }
}