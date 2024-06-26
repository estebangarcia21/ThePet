package org.bluehats.events

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer
import org.bluehats.util.*
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import org.bluehats.log
import java.util.Optional

class DamageDisplay : Listener {
    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val damaged = event.entity
        if (damaged is ArmorStand) return

        val damagedLocation = damaged.location
        damagedLocation.y = event.entity.location.y + 2.5

        val weapon = damager.inventory.itemInMainHand
        if (weapon.type.isAir) {
            return
        }

        val damages = getWeaponDamages(weapon)

        var attackDisplay = ""
        damages.sorted().forEach {
            val type = it.type
            val color = type.color()
            val symbol = type.symbol

            val damage = (it.min..it.max).random()

            attackDisplay += "<$color:bold>$symbol $damage</$color> ".colorize(noResetChars = true)
        }

        val radius = 2.5
        val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
        val offsetX = radius * cos(randomAngle)
        val offsetZ = radius * sin(randomAngle)
        val offsetY = Random.nextDouble(-0.25, 0.25)

        val holoLoc = damagedLocation.clone().add(offsetX, offsetY, offsetZ)
        val holoId = spawnClientSideEntity(damager, holoLoc, EntityType.TEXT_DISPLAY)

        var bitMask = 0x00
        bitMask = bitMask or 0x20

        setClientSideEntityMetadata(holoId, listOf(
            wrappedDataValue(5, Registry.get(java.lang.Boolean::class.java), true), // No gravity
            wrappedDataValue(15, Registry.get(java.lang.Byte::class.java), 3.toByte()),
            wrappedDataValue(23, Registry.getChatComponentSerializer(false), WrappedChatComponent.fromText(attackDisplay).handle), // Custom name
            wrappedDataValue(24, Registry.get(java.lang.Integer::class.java), 500),
        ))
    }

//    @EventHandler
//    fun onDamage(event: EntityDamageByEntityEvent) {
//        val damager = event.damager as? Player ?: return
//        val damaged = event.entity
//        if (damaged is ArmorStand) return
//
//        val damagedLocation = damaged.location
//        damagedLocation.y = event.entity.location.y + 2.5
//
//        val weapon = damager.inventory.itemInMainHand
//        if (weapon.type.isAir) {
//            return
//        }
//
//        val damages = getWeaponDamages(weapon)
//
//        var attackDisplay = ""
//        damages.sorted().forEach {
//            val type = it.type
//            val color = type.color()
//            val symbol = type.symbol
//
//            val damage = (it.min..it.max).random()
//
//            attackDisplay += "<$color:bold>$symbol $damage</$color> ".colorize(noResetChars = true)
//        }
//
//        val radius = 2.5
//        val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
//        val offsetX = radius * cos(randomAngle)
//        val offsetZ = radius * sin(randomAngle)
//        val offsetY = Random.nextDouble(-0.25, 0.25)
//
//        val randomLocation = damagedLocation.clone().add(offsetX, offsetY, offsetZ)
//
//        val dmgHologram = Hologram(listOf(attackDisplay.trim()), randomLocation)
//        dmgHologram.show()
//    }
}
