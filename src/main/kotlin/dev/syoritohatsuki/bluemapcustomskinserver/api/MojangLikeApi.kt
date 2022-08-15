package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.Profile
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

class MojangLikeApi(private val uuid: UUID) {
    fun getSkin(): CompletableFuture<BufferedImage> {

        val json = Json { ignoreUnknownKeys = true }

        return CompletableFuture<BufferedImage>().apply {
            Thread {
                json.decodeFromString<Profile>(
                    HttpClient.newHttpClient().send(
                        HttpRequest.newBuilder(URI.create(ConfigManager.read().customSkinServerUrl + uuid))
                            .build(), HttpResponse.BodyHandlers.ofString()
                    ).body()
                ).apply {
                    properties.find { it.name == "textures" }?.let {
                        json.decodeFromString<TextureInfo>(String(Base64.getDecoder().decode(it.value))).apply {
                            complete(ImageIO.read(URL(textures.skin.url)))
                        }
                    }
                }
            }.start()
        }
    }
}