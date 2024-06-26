package org.bluehats.util

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

const val DAMAGE_PREFIX = "damage"

enum class DamageType(private val element: String, val symbol: String) {
    NEUTRAL("neutral", "✤"),
    EARTH("earth", "✤"),
    THUNDER("thunder", "✦"),
    WATER("water", "❉"),
    FIRE("fire", "✹"),
    AIR("air", "❋");

    val nbtMinDmg = "${DAMAGE_PREFIX}_${element}_min"
    val nbtMaxDmg = "${DAMAGE_PREFIX}_${element}_max"

    fun lore(minDmg: Int, maxDmg: Int): String {
        val color = color()
        val el = element.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        return "<$color>$symbol $el</$color> Damage: $minDmg-$maxDmg"
    }

    fun color(): String {
        return when (this) {
            NEUTRAL -> "gold"
            EARTH -> "dark-green"
            THUNDER -> "yellow"
            WATER -> "aqua"
            FIRE -> "red"
            AIR -> "white"
        }
    }
}

data class DamageId(val type: DamageType, val min: Int, val max: Int) : Comparable<DamageId> {
    val lore = type.lore(min, max)

    override fun compareTo(other: DamageId): Int {
        return type.compareTo(other.type)
    }
}

fun setWeaponBaseDamage(weapon: ItemStack, vararg ids: DamageId) {
    ids.sorted().forEach {
        setNBTInt(weapon, it.type.nbtMinDmg, it.min)
        setNBTInt(weapon, it.type.nbtMaxDmg, it.max)
    }
}

fun getWeaponDamages(weapon: ItemStack): List<DamageId> {
    val ids = mutableListOf<DamageId>()

    DamageType.entries.forEach {
        if (hasNBTEntry(weapon, it.nbtMinDmg, PersistentDataType.INTEGER) && hasNBTEntry(weapon, it.nbtMaxDmg, PersistentDataType.INTEGER)) {
            val minDmg = getNBTInt(weapon, it.nbtMinDmg)!!
            val maxDmg = getNBTInt(weapon, it.nbtMaxDmg)!!

            ids += DamageId(it, minDmg, maxDmg)
        }
    }

    return ids
}

fun getWeaponDamagesLore(weapon: ItemStack): Array<String> {
    val damages = getWeaponDamages(weapon)

    return damages.map { it.lore }.toTypedArray()
}
