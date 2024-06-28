package org.bluehats.game

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import net.minecraft.core.Vector3f
import org.bluehats.events.PetDamageEvent
import org.bluehats.util.*
import org.bluehats.util.Timer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*
import kotlin.experimental.or
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

object DamageListener : Listener {
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val damaged = event.entity as? Player ?: return

        val e = PetDamageEvent(damager, damaged, event)

        // TODO: Check if has nbt tag "petweapon" = true

        Bukkit.getPluginManager().callEvent(e)
    }
}

object DamageManager : Listener {
    private val hologramLiveTimer = Timer<UUID>()

    @EventHandler
    fun onPetDamage(event: PetDamageEvent) {
        val damager = event.damager
        val damaged = event.damaged

        event.baseEvent.isCancelled = true
        damaged.damage(0.0)

        val damagerWeapon = damager.inventory.itemInMainHand
        if (damagerWeapon.type == Material.AIR) return

        val damageIds = getWeaponDamages(damagerWeapon)
        val damages = selectDamageFromDamageIDs(damageIds)

        damages.forEach {
            damaged.gameState.health -= it.amount
        }

        damaged.sendMessage("${damaged.gameState.health}")

        displayHologramDamage(damager, damaged.location, hologramLiveTimer, damages)
    }
}

private fun displayHologramDamage(damager: Player, damagedLocation: Location, timer: Timer<UUID>, damages: List<Damage>) {
    val attackDisplayList = mutableListOf<String>()
    damages.sorted().forEach {
        val type = it.type
        val color = type.color
        val symbol = type.symbol
        val damage = it.amount

        attackDisplayList += "<$color:bold>$symbol $damage</$color>".colorize(noResetChars = true)
    }

    val attackDisplay = attackDisplayList.joinToString(" ")

    val radius = 0.4
    val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
    val offsetX = radius * cos(randomAngle)
    val offsetZ = radius * sin(randomAngle)
    val offsetY = 1.75

    var bitMask = 0x00.toByte()
    bitMask = bitMask or 0x20

    val holoLoc = damagedLocation.clone().add(offsetX, offsetY, offsetZ)
//    val (hologramId, hologramUUID) = spawnClientSideEntity(damager, holoLoc, EntityType.ARMOR_STAND, metadata = listOf(
//        WrappedDataValue(0, BYTE_SERIALIZER, bitMask),
//        WrappedDataValue(2, CHAT_OPTIONAL_SERIALIZER, Optional.of(WrappedChatComponent.fromText(attackDisplay).handle)),
//        WrappedDataValue(3, BOOLEAN_SERIALIZER, true),
//        WrappedDataValue(5, BOOLEAN_SERIALIZER, false)
//    ))

    val vectorSerializer = WrappedDataWatcher.Registry.get(org.joml.Vector3f::class.java)

    val (hologramId, hologramUUID) = spawnClientSideEntity(damager, holoLoc, EntityType.TEXT_DISPLAY, metadata = listOf(
//        WrappedDataValue(12, vectorSerializer, org.joml.Vector3f(2f, 2f, 2f)),
        WrappedDataValue(15, BYTE_SERIALIZER, 3.toByte()),
        WrappedDataValue(23, CHAT_SERIALIZER, WrappedChatComponent.fromText(attackDisplay).handle),
        WrappedDataValue(24, INT_SERIALIZER, 500),
    ))

    val duration = 14 * TICKS
    timer.after(hologramUUID, duration, onTick = { tick ->
        val movementStep = 1 - tick.toDouble() / duration.toDouble()
        val dy = 1f + (holoMovementFunc(1*movementStep)).toFloat()

//        updateClientSideEntityPositionLT8(damager, hologramId, 0.0, dy, 0.0)

        setClientSideEntityMetadata(damager, hologramId, listOf(
            WrappedDataValue(12, vectorSerializer, org.joml.Vector3f(dy, dy, dy)),
            WrappedDataValue(15, BYTE_SERIALIZER, 3.toByte()),
            WrappedDataValue(23, CHAT_SERIALIZER, WrappedChatComponent.fromText(attackDisplay).handle),
            WrappedDataValue(24, INT_SERIALIZER, 500),
        ))
    }) {
        destroyClientSideEntity(damager, hologramId)
    }
}

private fun holoMovementFunc(x: Double): Double {
    val y = -1*(x.coerceAtMost(1.0).pow(2))

    return y
}
