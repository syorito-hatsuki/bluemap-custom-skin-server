package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {
    fun getSkin(): CompletableFuture<BufferedImage> {
        return CompletableFuture<BufferedImage>().apply {
            Thread {
                BlueMapCustomSkinServerAddon.LOGGER.info(ConfigManager.read().getSkinBy.toString())
                BlueMapCustomSkinServerAddon.LOGGER.info(ConfigManager.read().serverType.toString())
                BlueMapCustomSkinServerAddon.LOGGER.info(ConfigManager.read().customSkinServerUrl + name)
                BlueMapCustomSkinServerAddon.LOGGER.info(name)
                BlueMapCustomSkinServerAddon.LOGGER.info(uuid.toString())
                when (ConfigManager.read().getSkinBy) {
                    Config.SkinBy.UUID -> {
                        (ConfigManager.read().customSkinServerUrl + uuid).apply {
                            BlueMapCustomSkinServerAddon.LOGGER.info(this)
                            complete(ImageIO.read(URL(this)))
                        }
                    }
                    Config.SkinBy.NAME -> {
                        (ConfigManager.read().customSkinServerUrl + getName() + ConfigManager.read().suffix).apply {
                            BlueMapCustomSkinServerAddon.LOGGER.info(this)
                            complete(ImageIO.read(URL(this)))
                        }
                    }
                }
            }.start()
        }
    }

    private fun getName(): String {
        return when (ConfigManager.read().skinByCase) {
            Config.SkinByCase.UPPER -> name.uppercase()
            Config.SkinByCase.LOWER -> name.lowercase()
            Config.SkinByCase.DEFAULT -> name
        }
    }
}