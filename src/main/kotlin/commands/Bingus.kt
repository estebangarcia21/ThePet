package org.bluehats.commands

import org.bluehats.util.*
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class Bingus : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = sender as Player

        val sword = ItemStack(Material.GOLDEN_SWORD)

        setWeaponBaseDamage(
            sword,
            DamageId(DamageType.NEUTRAL, 0, 1),
            DamageId(DamageType.EARTH, 0, 1),
            DamageId(DamageType.THUNDER, 0, 1),
            DamageId(DamageType.WATER, 0, 1),
            DamageId(DamageType.FIRE, 0, 1),
            DamageId(DamageType.AIR, 0, 1)
        )

        val meta = sword.itemMeta!!
        meta.isUnbreakable = true

        meta.lore = buildLore(
            "Super Fast Attack Speed",
            "",
            *getWeaponDamagesLore(sword),
            "",
            "<yellow>Default Item</yellow>",
            "Your first item in <italic>The Pet</italic>, you monkey!"
        )
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        meta.setDisplayName("<yellow>Monkey Sword</yellow>".colorize())

        sword.itemMeta = meta

        player.inventory.addItem(sword)

        return true
    }
}
