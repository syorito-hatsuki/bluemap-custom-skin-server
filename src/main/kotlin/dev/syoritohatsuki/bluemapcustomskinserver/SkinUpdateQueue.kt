package dev.syoritohatsuki.bluemapcustomskinserver

import de.bluecolored.bluemap.common.plugin.Plugin
import dev.syoritohatsuki.bluemapcustomskinserver.BlueMapCustomSkinServerAddon.logger
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object SkinUpdateQueue {
    private var bluemapPluginInstance: Plugin? = null
    private val queue: MutableSet<UUID> = Collections.newSetFromMap(ConcurrentHashMap())

    fun contains(uuid: UUID): Boolean {
        val result = uuid in queue
        logger.info("[$uuid] Contains: $result")
        return result
    }

    fun add(uuid: UUID): Boolean {
        logger.info("[$uuid] Add")
        return queue.add(uuid)
    }

    fun remove(uuid: UUID): Boolean {
        logger.info("[$uuid] Remove")
        return queue.remove(uuid)
    }

    fun registerBluemapPluginInstance(bluemapPluginInstance: Plugin) {
        logger.info("Catch bluemapPluginInstance")
        this.bluemapPluginInstance = bluemapPluginInstance
    }

    fun getBluemapPluginInstance(): Plugin? = bluemapPluginInstance
}
