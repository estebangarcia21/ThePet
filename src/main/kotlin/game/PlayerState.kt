package org.bluehats.game

import org.bukkit.entity.Player
import kotlin.math.roundToInt
import java.util.UUID
private const val BASE_HEALTH = 1000.0

class PlayerState(private val player: Player) {
    private val maxHealth = BASE_HEALTH

    var health: Double = maxHealth
        set(value) {
            field = value.coerceIn(0.0, maxHealth)
            if (field == 0.0) {
                kill()
            }
        }

    val minecraftHealth: Int
        get() = (health / maxHealth).roundToInt() * 20

    fun reset() {
        health = maxHealth
    }

    private fun kill() {
        player.health = 0.0

        reset()
    }

//    fun attackDownTime(item: ItemStack, timer: Timer<UUID> )
}
