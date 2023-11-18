package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.debugMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {
    fun getSkin(): CompletableFuture<BufferedImage> = CompletableFuture<BufferedImage>().apply {
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                val config = ConfigManager.read()
                config.let {
                    logger.debugMessage(it.url)
                    logger.debugMessage(it.serverType.name)
                }
                logger.debugMessage(name)
                logger.debugMessage(uuid.toString())
                ConfigManager.read().url.replace("%uuid%", uuid.toString()).replace("%username%", name).let {
                    logger.debugMessage(it)
                    complete(ImageIO.read(URL(it)))
                }
            }.onSuccess {
                logger.info("Skin loaded: $it")
            }.onFailure {
                logger.warn(it.message)
            }
        }
    }
}