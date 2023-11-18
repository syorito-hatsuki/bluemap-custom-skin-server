package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.debugMessage
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.Profile
import dev.syoritohatsuki.bluemapcustomskinserver.dto.mojang.TextureInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    fun getSkin(): CompletableFuture<BufferedImage> = CompletableFuture<BufferedImage>().apply {
        val json = Json { ignoreUnknownKeys = true }
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                json.decodeFromString<Profile>(withContext(Dispatchers.IO) {
                    URI.create(ConfigManager.read().url.replace("%uuid%", uuid.toString())).let {
                        logger.debugMessage(it.toString())
                        HttpClient.newHttpClient()
                            .send(HttpRequest.newBuilder(it).build(), HttpResponse.BodyHandlers.ofString()).body()
                    }
                })
            }.onSuccess { profile ->
                profile.properties.find { it.name == "textures" }?.let {
                    json.decodeFromString<TextureInfo>(String(Base64.getDecoder().decode(it.value))).apply {
                        logger.debugMessage(textures.skin.url)
                        complete(ImageIO.read(URL(textures.skin.url)))
                    }
                }
                logger.info("Skin loaded successful")
            }.onFailure {
                logger.warn(it.message)
            }
        }
    }
}