package org.bluehats.commands

import org.bluehats.util.*
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object Bingus : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player

        val sword = ItemStack(Material.GOLDEN_SWORD)

        val weapon = Weapon(sword, "The Wrath of a Monkey", Rarity.MYTHIC, WeaponType.SWORD, AttackSpeed.SUPER_SLOW, listOf(
            DamageId(DamageType.NEUTRAL, 10, 32),
            DamageId(DamageType.LIGHT, 20, 47),
            DamageId(DamageType.DARK, 0, 666),
        ), "You monkey!")

        weapon.applyItemData()

        player.inventory.addItem(sword)

        return true
    }
}
