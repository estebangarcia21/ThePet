package org.bluehats.commands

import org.bluehats.util.*
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object Bingus : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player

        val sword = ItemStack(Material.GOLDEN_SWORD)

        setWeaponBaseDamage(
            sword,
            DamageId(DamageType.NEUTRAL, 15, 44),
            DamageId(DamageType.LIGHT, 6, 12),
            DamageId(DamageType.DARK, 42, 90)
        )

        val meta = sword.itemMeta!!
        meta.isUnbreakable = true

        meta.lore = buildLore(
            "<bold>Super Fast Attack Speed</bold>",
            "",
            *getWeaponDamagesLore(sword),
            "",
            "<dark-purple>Default Item</dark-purple>",
            "Your first item in <italic>The Pet</italic>, you monkey!"
        )
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        meta.setDisplayName("<dark-purple>Monkey Sword</dark-purple>".colorize())

        sword.itemMeta = meta

        player.inventory.addItem(sword)

        return true
    }
}
