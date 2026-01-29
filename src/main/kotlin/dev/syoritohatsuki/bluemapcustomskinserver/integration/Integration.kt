package dev.syoritohatsuki.bluemapcustomskinserver.integration

import java.awt.image.BufferedImage
import java.util.*
import java.util.concurrent.CompletableFuture

interface Integration {
    fun getSkin(uuid: UUID, username: String): CompletableFuture<BufferedImage?>
}