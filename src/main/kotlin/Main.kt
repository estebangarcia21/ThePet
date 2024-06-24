package org.bluehats

import org.bukkit.plugin.java.JavaPlugin

class ThePet : JavaPlugin() {
    override fun onEnable() {
        logger.info("-------------------------- ThePet has been enabled")
    }

    override fun onDisable() {
        logger.info("ThePet has been disabled")
    }
}
