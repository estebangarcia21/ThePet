package org.bluehats.events

import org.bluehats.util.getNBTFloat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class DamageModification : Listener {
    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return

        val item = damager.inventory.itemInMainHand
        if (item.type.isAir) {
            damager.sendMessage("You need to hold an item in your main hand.")
            return
        }

        val damage = getNBTFloat(item, "damage_neutral") ?: "no current"

        damager.sendMessage("You have $damage neutral damage.")
    }
}
