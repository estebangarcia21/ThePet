package org.bluehats.util

import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

const val DAMAGE_PREFIX = "damage"

data class Weapon(val weapon: ItemStack, val name: String, val rarity: Rarity, val type: WeaponType, val attackSpeed: AttackSpeed, val damages: List<DamageId>, val description: String = "") {
    companion object {
        private const val NAME_NBT_KEY = "name"
        private const val DESCRIPTION_NBT_KEY = "name"

        /**
         * @return Weapon if a valid game weapon, else null.
         */
        fun fromItem(weapon: ItemStack): Weapon? {
            val name = getNBTString(weapon, NAME_NBT_KEY) ?: return null
            val description = getNBTString(weapon, DESCRIPTION_NBT_KEY) ?: return null
            val type = getNBTString(weapon, WeaponType.NBT_KEY) ?: return null
            val attackSpeed = getNBTString(weapon, AttackSpeed.NBT_KEY) ?: return null
            val rarity = getNBTString(weapon, Rarity.NBT_KEY) ?: return null

            val damages = mutableListOf<DamageId>()

            DamageType.entries.filter { hasNBTEntry(weapon, it.nbtMinDmg, PersistentDataType.INTEGER) && hasNBTEntry(weapon, it.nbtMaxDmg, PersistentDataType.INTEGER) }.forEach {
                val minDmg = getNBTInt(weapon, it.nbtMinDmg)!!
                val maxDmg = getNBTInt(weapon, it.nbtMaxDmg)!!

                damages += DamageId(it, minDmg, maxDmg)
            }

            return Weapon(
                weapon = weapon,
                name = name,
                rarity = Rarity.fromDisplayName(rarity),
                type = WeaponType.fromTypeName(type),
                attackSpeed = AttackSpeed.fromDisplayName(attackSpeed),
                damages = damages,
                description = description
            )
        }
    }

    private val rarityColor = rarity.color

    fun applyItemData() {
        val meta = weapon.itemMeta!!

        meta.isUnbreakable = true
        meta.lore = lore()

        meta.setDisplayName("<$rarityColor>$name</$rarityColor>".colorize())
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        weapon.itemMeta = meta

        setNBTString(weapon, NAME_NBT_KEY, name)
        setNBTString(weapon, DESCRIPTION_NBT_KEY, description)
        setNBTString(weapon, WeaponType.NBT_KEY, type.typeName)
        setNBTString(weapon, AttackSpeed.NBT_KEY, attackSpeed.displayName)
        setNBTString(weapon, Rarity.NBT_KEY, rarity.displayName)

        damages.sorted().forEach {
            setNBTInt(weapon, it.type.nbtMinDmg, it.min)
            setNBTInt(weapon, it.type.nbtMaxDmg, it.max)
        }
    }

    val randomDamages: List<Damage>
        get() = damages.map { Damage(it.type, (it.min..it.max).random()) }

    private fun lore(): List<String> {
        val attackSpeedLore = attackSpeed.lore
        val damageLore = damages.map { it.lore }.toTypedArray()

        val rarityTitle = rarity.displayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        var lore = buildLore(
            attackSpeedLore,
            "",
            *damageLore,
            "",
            "<$rarityColor>$rarityTitle Item</$rarityColor>",
        )

        if (description != "") lore = lore + description

        return lore
    }
}

enum class Rarity(val displayName: String, val color: String) {
    DEFAULT("default", "white"),
    UNIQUE("unique", "yellow"),
    RARE("rare", "light-purple"),
    LEGENDARY("legendary", "aqua"),
    MYTHIC("mythic", "dark-purple");

    companion object {
        const val NBT_KEY = "rarity"

        fun fromDisplayName(n: String): Rarity = Rarity.entries.first { it.displayName == n }
    }
}

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

enum class AttackSpeed(val displayName: String, val attackDelayTicks: Tick, val speedLevel: Int) {
    SUPER_SLOW("Super Slow", 39L, -17),
    VERY_SLOW("Very Slow", 24L, -13),
    SLOW("Slow", 13L, -9),
    NORMAL("Normal", 10L, -5),
    FAST("Fast", 8L, -4),
    VERY_FAST("Very Fast", 6L, 0),
    SUPER_FAST("Super Fast" ,4L, 2);

    val fullDisplayName = "$displayName Attack Speed"
    val lore = "<gray:bold>$fullDisplayName</gray:bold>"

    companion object {
        const val NBT_KEY = "attack_speed"

        fun fromDisplayName(n: String): AttackSpeed = entries.first { it.displayName == n }
    }
}

enum class WeaponType(val typeName: String) {
    SWORD("sword"),
    BOW("bow");

    companion object {
        const val NBT_KEY = "weapon_type"

        fun fromTypeName(n: String): WeaponType = entries.first { it.typeName == n }
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
