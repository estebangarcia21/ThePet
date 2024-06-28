package org.bluehats.game

import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedDataValue
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
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

object DamageManager : Listener {
    private val playerAttackTimer = Timer<UUID>()
    private val hologramLiveTimer = Timer<UUID>()

    @EventHandler
    fun onPetDamage(event: PetDamageEvent) {
        val damager = event.damager
        val damaged = event.damaged

        event.baseEvent.isCancelled = true
        damaged.damage(0.0)

        val damagerWeapon = damager.inventory.itemInMainHand
        if (damagerWeapon.type == Material.AIR) return

        val weapon = Weapon.fromItem(damagerWeapon) ?: return
        val damages = weapon.randomDamages

        playerAttackTimer.cooldown(
            id = damager.uniqueId,
            ticks = weapon.attackSpeed.attackDelayTicks,
            resetTime = false
        ) {
            damages.forEach {
                damaged.gameState.health -= it.amount
            }

            damaged.sendMessage("${damaged.gameState.health}")
            damaged.damage(0.0)

            displayHologramDamage(damager, damaged.location, hologramLiveTimer, damages)
        }
    }
}

object DamageListener : Listener {
    @EventHandler
    fun onEntityDamageEvent(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        val damaged = event.entity as? Player ?: return


        val e = PetDamageEvent(damager, damaged, event)

        Bukkit.getPluginManager().callEvent(e)
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

    val radius = 0.75
    val randomAngle = Random.nextDouble(0.0, 2 * Math.PI)
    val offsetX = radius * cos(randomAngle)
    val offsetZ = radius * sin(randomAngle)
    val offsetY = 1.75

    val holoLoc = damagedLocation.clone().add(offsetX, offsetY, offsetZ)
    val (hologramId, hologramUUID) = spawnClientSideEntity(damager, holoLoc, EntityType.TEXT_DISPLAY, metadata = listOf(
        WrappedDataValue(9, INT_SERIALIZER, 60),
        WrappedDataValue(10, INT_SERIALIZER, 20),
        WrappedDataValue(15, BYTE_SERIALIZER, 3.toByte()),
        WrappedDataValue(23, CHAT_SERIALIZER, WrappedChatComponent.fromText(attackDisplay).handle),
        WrappedDataValue(24, INT_SERIALIZER, 500),
        WrappedDataValue(25, INT_SERIALIZER, 0xFF000000.toInt()),
    ))

    val duration = 14 * TICKS
    timer.after(hologramUUID, duration, onTick = { tick ->
        val movementStep = 1 - tick.toDouble() / duration.toDouble()
        val dy = 1f + (holoMovementFunc(1*movementStep)).toFloat()

        updateClientSideEntityPositionLT8(damager, hologramId, 0.0, -dy.toDouble() / 9.0, 0.0)

        setClientSideEntityMetadata(damager, hologramId, listOf(
            WrappedDataValue(12, VECTOR_SERIALIZER, org.joml.Vector3f(dy, dy, dy)),
        ))
    }) {
        destroyClientSideEntity(damager, hologramId)
    }
}

private fun holoMovementFunc(x: Double): Double {
    val y = -1*(x.coerceAtMost(1.0).pow(2))

    return y
}
