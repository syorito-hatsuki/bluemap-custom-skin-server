package dev.syoritohatsuki.bluemapcustomskinserver

import de.bluecolored.bluemap.common.plugin.Plugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object SkinUpdateQueue {
    private var bluemapPluginInstance: Plugin? = null
    private val queue: MutableSet<UUID> = Collections.newSetFromMap(ConcurrentHashMap())

    fun contains(uuid: UUID): Boolean {
        val result = uuid in queue
        println("[$uuid] Contains: $result")
        return result
    }

    fun add(uuid: UUID): Boolean {
        println("[$uuid] Add")
        return queue.add(uuid)
    }

    fun remove(uuid: UUID): Boolean {
        println("[$uuid] Remove")
        return queue.remove(uuid)
    }

    fun registerBluemapPluginInstance(bluemapPluginInstance: Plugin) {
        println("Catch bluemapPluginInstance")
        this.bluemapPluginInstance = bluemapPluginInstance
    }

    fun getBluemapPluginInstance(): Plugin? = bluemapPluginInstance
}
