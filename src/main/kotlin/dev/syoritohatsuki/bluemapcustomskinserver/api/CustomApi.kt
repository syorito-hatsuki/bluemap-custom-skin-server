package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.LOGGER
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.playerList
import dev.syoritohatsuki.bluemapcustomskinserver.config.Config
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.debugMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID) {

    private lateinit var name: String

    fun getSkin(): CompletableFuture<BufferedImage> {
        return CompletableFuture<BufferedImage>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    name = playerList[uuid].toString()

                    debugMode(ConfigManager.read().custom.getSkinBy.toString())
                    debugMode(ConfigManager.read().custom.skinByCase.toString())
                    debugMode(ConfigManager.read().customSkinServerUrl)
                    debugMode(ConfigManager.read().custom.suffix)
                    debugMode(name)
                    debugMode(uuid.toString())

                    when (ConfigManager.read().custom.getSkinBy) {
                        Config.Custom.SkinBy.UUID -> {
                            (ConfigManager.read().customSkinServerUrl + uuid).apply {

                                debugMode(this)

                                complete(ImageIO.read(URL(this)))
                            }
                        }
                        Config.Custom.SkinBy.NAME -> {
                            (ConfigManager.read().customSkinServerUrl + getName() + ConfigManager.read().custom.suffix).apply {

                                debugMode(this)

                                complete(ImageIO.read(URL(this)))
                            }
                        }
                    }
                }.onSuccess {
                    LOGGER.info("Skin loaded: $it")
                }.onFailure {
                    LOGGER.error(it.message)
                }
            }
        }
    }

    private fun getName(): String {
        return when (ConfigManager.read().custom.skinByCase) {
            Config.Custom.SkinByCase.UPPER -> name.uppercase()
            Config.Custom.SkinByCase.LOWER -> name.lowercase()
            Config.Custom.SkinByCase.DEFAULT -> name
        }
    }
}