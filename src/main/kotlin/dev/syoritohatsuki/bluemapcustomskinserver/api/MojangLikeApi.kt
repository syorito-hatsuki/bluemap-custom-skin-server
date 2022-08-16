package dev.syoritohatsuki.bluemapcustomskinserver.api

import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.LOGGER
import dev.syoritohatsuki.bluemapcustomskinserver.config.ConfigManager
import dev.syoritohatsuki.bluemapcustomskinserver.debugMode
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
    fun getSkin(): CompletableFuture<BufferedImage> {

        val json = Json { ignoreUnknownKeys = true }

        return CompletableFuture<BufferedImage>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    val body = withContext(Dispatchers.IO) {
                        val uri = URI.create(ConfigManager.read().customSkinServerUrl + uuid)

                        debugMode(uri.toString())

                        HttpClient.newHttpClient()
                            .send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString()).body()
                    }
                    json.decodeFromString<Profile>(body)
                }.onSuccess { profile ->
                    profile.properties.find { it.name == "textures" }?.let {
                        json.decodeFromString<TextureInfo>(String(Base64.getDecoder().decode(it.value))).apply {

                            debugMode(textures.skin.url)

                            complete(ImageIO.read(URL(textures.skin.url)))
                        }
                    }
                    LOGGER.info("Skin loaded successful")
                }.onFailure {
                    LOGGER.error(it.message)
                }
            }
        }
    }
}