package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.debug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {

    fun getSkin(): CompletableFuture<BufferedImage> {
        return CompletableFuture<BufferedImage>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {

                    debug(ConfigManager.read().custom.getSkinBy.toString())
                    debug(ConfigManager.read().custom.skinByCase.toString())
                    debug(ConfigManager.read().customSkinServerUrl)
                    debug(ConfigManager.read().custom.suffix)
                    debug(name)
                    debug(uuid.toString())

                    when (ConfigManager.read().custom.getSkinBy) {

                        Config.Custom.SkinBy.UUID -> {
                            (ConfigManager.read().customSkinServerUrl + uuid).apply {

                                debug(this)

                                complete(ImageIO.read(URL(this)))
                            }
                        }

                        Config.Custom.SkinBy.NAME -> {
                            (ConfigManager.read().customSkinServerUrl + getName() + ConfigManager.read().custom.suffix).apply {

                                debug(this)

                                complete(ImageIO.read(URL(this)))
                            }
                        }

                    }
                }.onSuccess {
                    logger.info("Skin loaded: $it")
                }.onFailure {
                    logger.warn(it.message)
                }
            }
        }
    }

    private fun getName(): String = when (ConfigManager.read().custom.skinByCase) {
        Config.Custom.SkinByCase.UPPER -> name.uppercase()
        Config.Custom.SkinByCase.LOWER -> name.lowercase()
        Config.Custom.SkinByCase.DEFAULT -> name
    }

}