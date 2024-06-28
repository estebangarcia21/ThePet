package org.bluehats.events

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageByEntityEvent

class PetDamageEvent(val damager: Player, val damaged: Player, val baseEvent: EntityDamageByEntityEvent) : Event() {
    companion object {
        private val handlers: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return PetDamageEvent.handlers
    }
}
