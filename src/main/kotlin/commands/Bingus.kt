package org.bluehats.commands

import org.bluehats.util.buildLore
import org.bluehats.util.colorize
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

        val sword = ItemStack(Material.WOODEN_SWORD)

        val meta = sword.itemMeta!!
        meta.isUnbreakable = true

        meta.lore = buildLore("""
            Super Slow Attack Speed
            <gold>✤ Neutral</gold> Damage: 999-1026
            <dark-green>✤ Earth</dark-green> Damage: 1027-1652
            <yellow>✦ Thunder</yellow> Damage: 0-1102
            <aqua>❉ Water</aqua> Damage: 52-120
            <red>✹ Fire</red> Damage: 200-512
            <white>❋ Air</white> Damage: 100-100
            <red>Default Item</red>
            Your first item in <italic>The Pet</italic>."""
        )
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        meta.setDisplayName("<red>Magical Stick</red>".colorize())

        sword.itemMeta = meta

        player.inventory.addItem(sword)

        return true
    }
}
