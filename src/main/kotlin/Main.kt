package org.bluehats

import org.bluehats.commands.Bingus
import org.bluehats.events.DamageModification
import org.bukkit.plugin.java.JavaPlugin

class ThePet : JavaPlugin() {
    override fun onEnable() {
        logger.info("-------------------------- ThePet has been enabled")
        this.getCommand("bingus")?.setExecutor(Bingus())
        this.server.pluginManager.registerEvents(DamageModification(), this)
    }

    override fun onDisable() {
        logger.info("ThePet has been disabled")
    }
}
