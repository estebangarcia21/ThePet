package org.bluehats

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import org.bluehats.commands.Bingus
import org.bluehats.events.DamageDisplay
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class ThePet : JavaPlugin() {
    companion object {
        lateinit var plugin: JavaPlugin
        lateinit var protocolManager: ProtocolManager
    }

    override fun onEnable() {
        plugin = this

        logger.info("-------------------------- ThePet has been enabled")
        this.getCommand("bingus")?.setExecutor(Bingus())
        this.server.pluginManager.registerEvents(DamageDisplay(), this)
    }

    override fun onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onDisable() {
        logger.info("ThePet has been disabled")
    }
}

fun registerEvents(clazz: Listener) {
    ThePet.plugin.server.pluginManager.registerEvents(clazz, ThePet.plugin)
}

fun log(msg: String) {
    ThePet.plugin.logger.info(msg)
}
