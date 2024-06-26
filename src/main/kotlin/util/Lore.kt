package org.bluehats.util

import org.bukkit.ChatColor
import java.util.*

fun String.colorize(defaultColor: ChatColor = ChatColor.GRAY): String {
    var acc = defaultColor.toString() + this

    ChatColor.entries.forEach { c ->
        val name = c.name.lowercase(Locale.getDefault()).replace("_", "-")

        acc = acc.replace("<$name>", c.toString())
        acc = acc.replace("</$name>", defaultColor.toString())

        acc = acc.replace("<$name:bold>", c.toString() + ChatColor.BOLD.toString())
        acc = acc.replace("</$name:bold>", ChatColor.RESET.toString() + defaultColor.toString())

        acc += defaultColor.toString()
    }

    return acc
}

fun buildLore(vararg lines: String, defaultColor: ChatColor = ChatColor.GRAY): List<String> = lines.map { l -> l.colorize(defaultColor) }
fun buildLore(lines: String, defaultColor: ChatColor = ChatColor.GRAY): List<String> = lines.trimIndent().split('\n').map { l -> l.colorize(defaultColor) }
