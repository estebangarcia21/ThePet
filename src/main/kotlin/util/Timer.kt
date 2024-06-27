package org.bluehats.util

import org.bluehats.ThePet
import org.bukkit.Bukkit

typealias Tick = Long

const val TICKS = 1L
const val SECONDS = 20L * TICKS
const val MINUTES = 60L * SECONDS
const val HOURS = 60L * MINUTES
const val DAYS = 24L * HOURS

/**
 * Inspired by Go's `time.Duration` interface.
 */
class Time(private val value: Tick) {
    fun ticks(): Long = value
    fun seconds(): Long = value / SECONDS
    fun minutes(): Long = value / MINUTES
    fun hours(): Long = value / HOURS
    fun days(): Long = value / DAYS
}

class Timer<K> {
    /**
     * The raw time amount for a player in ticks.
     */
    private val timer = mutableMapOf<K, Tick>()

    /**
     * Map of a player UUID to a Bukkit Task ID created by the scheduler.
     */
    private val tasks = mutableMapOf<K, Int>()

    val items: MutableSet<K>
        get() = timer.keys

    /**
     * Runs code after a certain amount of time.
     */
    fun after(
        id: K,
        ticks: Tick,
        onTick: ((Tick) -> Unit)? = null,
        resetTime: Boolean = false,
        operation: (() -> Unit)
    ) {
        setCooldown(id, ticks, resetTime, onTick, operation)
    }

    /**
     * Runs the code immediately, then prevents it from running again for the specified cooldown.
     */
    fun cooldown(
        id: K,
        ticks: Tick,
        post: (() -> Unit)? = null,
        resetTime: Boolean = false,
        onTick: ((Tick) -> Unit)? = null,
        cooldownAction: (() -> Unit)? = null,
        operation: () -> Unit
    ) {
        val cooldown = getCooldown(id)
        if (cooldown == null) {
            operation()

            setCooldown(id, ticks, resetTime, onTick, post)
        } else if (cooldownAction != null) {
            cooldownAction()
        }
    }
    /**
     *
     *
     */
    fun setCooldown(id: K, time: Tick, resetTime: Boolean, onTick: ((Tick) -> Unit)?, post: Runnable?) {
        if (getCooldown(id) == null || resetTime) {
            timer[id] = time
        }

        if (tasks.contains(id)) return
        scheduleTimer(id, post, onTick)
    }

    /**
     * Returns the cooldown time if it exists, otherwise null
     */
    fun getCooldown(id: K): Tick? {
        return timer[id]
    }

    fun reduceCooldown(id: K, ticksToReduce: Tick) {
        val currentCooldown = getCooldown(id) ?: return

        val newCooldown = (currentCooldown - ticksToReduce).coerceAtLeast(0L)

        timer[id] = newCooldown
    }

    fun stop(id: K) {
        val taskId = tasks[id] ?: return

        Bukkit.getScheduler().cancelTask(taskId)
        timer.remove(id)
        tasks.remove(id)
    }

    private fun scheduleTimer(id: K, post: Runnable?, onTick: ((Tick) -> Unit)? = null) {
        val rate = 1L
        val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(ThePet.plugin, {
            val currentTime = timer[id] ?: error("Attempted to schedule a timer with a null player UUID")
            val newTime = currentTime - rate
            val isFinalTick = newTime <= 0

            if (onTick != null) onTick(currentTime)

            if (isFinalTick) {
                stop(id)
                post?.run()
                return@scheduleSyncRepeatingTask
            }

            timer[id] = newTime
        }, 0L, rate)

        tasks[id] = taskId
    }
}
