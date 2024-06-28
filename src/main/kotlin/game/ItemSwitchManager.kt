package org.bluehats.game

import org.bluehats.util.Weapon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ItemSwitchManager : Listener {
    @EventHandler
    fun onPlayerItemSwitch(event: PlayerSwapHandItemsEvent) {
        val player: Player = event.player
        val mainHandItem: ItemStack = player.inventory.itemInMainHand

        val weapon = Weapon.fromItem(mainHandItem) ?: return
        var speedLevel = weapon.attackSpeed.speedLevel

        if (speedLevel < 0) {
            speedLevel *= -1
            player.addPotionEffect(PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, speedLevel))
        } else if (speedLevel > 0 ) {
            player.addPotionEffect(PotionEffect(PotionEffectType.HASTE, Integer.MAX_VALUE, speedLevel))
        }
    }
}