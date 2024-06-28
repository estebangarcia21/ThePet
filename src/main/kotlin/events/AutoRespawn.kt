package org.bluehats.events

import org.bluehats.ThePet
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object AutoRespawn : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, 0))
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity
        event.deathMessage = ""

        Bukkit.getServer().scheduler.scheduleSyncDelayedTask(ThePet.plugin, {
            player.spigot().respawn()
            player.foodLevel = 19
        }, 1L)
    }
}
