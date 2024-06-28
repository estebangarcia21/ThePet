package org.bluehats.util

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

const val DAMAGE_PREFIX = "damage"

enum class DamageType(private val element: String, val symbol: String, val color: String) {
    NEUTRAL("neutral", "\uD83D\uDDE1", "gold"),
    LIGHT("light", "✦", "white"),
    DARK("dark", "✤", "dark-purple");

    val nbtMinDmg = "${DAMAGE_PREFIX}_${element}_min"
    val nbtMaxDmg = "${DAMAGE_PREFIX}_${element}_max"

    fun lore(minDmg: Int, maxDmg: Int): String {
        val el = element.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        return "<$color:bold>$symbol $el</$color> Damage: $minDmg-$maxDmg"
    }
}

data class DamageId(val type: DamageType, val min: Int, val max: Int) : Comparable<DamageId> {
    val lore = type.lore(min, max)

    override fun compareTo(other: DamageId): Int {
        return type.compareTo(other.type)
    }
}

data class Damage(val type: DamageType, val amount: Int) : Comparable<Damage> {
    override fun compareTo(other: Damage): Int {
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

fun selectDamageFromDamageIDs(ids: List<DamageId>): List<Damage> = ids.map { Damage(it.type, (it.min..it.max).random()) }
