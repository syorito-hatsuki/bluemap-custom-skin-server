package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.LOGGER
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.config.SkinBy
import java.awt.image.BufferedImage
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class CustomApi(private val uuid: UUID, private val name: String) {
    fun getSkin(): CompletableFuture<BufferedImage> {
        val image = CompletableFuture<BufferedImage>()
        Thread {
            when (ConfigManager.read().getSkinBy) {
                SkinBy.UUID -> {
                    try {
                        val server = HttpClient.newHttpClient().send(
                            HttpRequest.newBuilder()
                                .uri(URI.create(ConfigManager.read().customSkinServerUrl + uuid))
                                .build(), HttpResponse.BodyHandlers.ofString()
                        ).body()
                        image.complete(ImageIO.read(URL(server)))
                        LOGGER.info("Connecting to custom skin server")
                        LOGGER.info("Server URL: ${ConfigManager.read().customSkinServerUrl}")
                    } catch (exception: Exception) {
                        LOGGER.warn("Incorrect skin server or skin not found")
                        LOGGER.warn(exception.message)
                    }
                }
                SkinBy.NAME -> {
                    image.complete(ImageIO.read(URL(ConfigManager.read().customSkinServerUrl + name)))
                }
            }
        }.start()
        return image
    }
}