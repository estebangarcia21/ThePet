package org.bluehats.events

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
import org.bluehats.util.*
import org.bluehats.util.Timer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*
import kotlin.experimental.or
import kotlin.math.*
import kotlin.random.Random

class DamageDisplay : Listener {
    private val timer = Timer<Int>()

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val damaged = event.entity

        val damagedLocation = damaged.location
        damagedLocation.y = event.entity.location.y + 0.5

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

        attackDisplay = attackDisplay.trim()

        val radius = 0.25
        val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
        val offsetX = radius * cos(randomAngle)
        val offsetZ = radius * sin(randomAngle)
        val offsetY = 0.0

        val holoLoc = damagedLocation.clone().add(offsetX, offsetY, offsetZ)
        val hologramId = spawnClientSideEntity(damager, holoLoc, EntityType.ARMOR_STAND)

        var bitMask = 0x00.toByte()
        bitMask = bitMask or 0x20

        setClientSideEntityMetadata(hologramId, listOf(
            WrappedDataValue(0, BYTE_SERIALIZER, bitMask),
            WrappedDataValue(2, CHAT_OPTIONAL_SERIALIZER, Optional.of(WrappedChatComponent.fromText(attackDisplay).handle)),
            WrappedDataValue(3, BOOLEAN_SERIALIZER, true),
            WrappedDataValue(5, BOOLEAN_SERIALIZER, false)
        ))

        val duration = 14 * TICKS // 1.5s
        timer.after(hologramId, duration, onTick = { tick ->
            val movementStep = 1 - tick.toDouble() / duration.toDouble()
            val dy = csEntityMovement(1.4*movementStep) / 2.5

            updateClientSideEntityPositionLT8(damager, hologramId, 0.0, dy, 0.0)
        }) {
            destroyClientSideEntity(damager, hologramId)
        }
    }
}

fun csEntityMovement(x: Double): Double {
    val y = -1*(x.coerceAtMost(1.0).pow(2))

    return y
}

