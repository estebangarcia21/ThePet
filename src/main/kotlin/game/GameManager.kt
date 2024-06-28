package org.bluehats.game

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import java.util.UUID

object GameManager : Listener {
    private val playerStates = hashMapOf<UUID, PlayerState>()

    fun playerState(player: Player): PlayerState = playerStates.getOrPut(player.uniqueId) { PlayerState(player) }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        playerStates[event.player.uniqueId] = PlayerState(event.player)
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        playerStates.remove(event.player.uniqueId)
    }

}

val Player.gameState: PlayerState
    get() = GameManager.playerState(this)
