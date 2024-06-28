package org.bluehats.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.core.Vector3f
import org.bluehats.ThePet
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * @return The client-side entity ID.
 */
fun spawnClientSideEntity(player: Player, location: Location, type: EntityType, overrideEntityId: Int? = null, metadata: List<WrappedDataValue> = emptyList()): Pair<Int, UUID> {
    val entityId = overrideEntityId ?: (0..Int.MAX_VALUE).random()
    val entityUUID = UUID.randomUUID()
    val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)

    packet.integers.write(0, entityId)
    packet.uuiDs.write(0, entityUUID)
    packet.entityTypeModifier.write(0, type)

    packet.doubles
        .write(0, location.x)
        .write(1, location.y)
        .write(2, location.z)

    val metadataPacket = if (metadata.isNotEmpty()) {
        val pkt = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)

        pkt.integers.write(0, entityId)
        pkt.dataValueCollectionModifier.write(0, metadata)

        pkt
    } else null

    ThePet.protocolManager.sendServerPacket(player, packet)
    if (metadataPacket != null) ThePet.protocolManager.sendServerPacket(player, metadataPacket)

    return entityId to entityUUID
}

/**
 * Update an entity position <= 8 blocks of delta movement on one or more axis.
 */
fun updateClientSideEntityPositionLT8(player: Player, entityId: Int, dx: Double, dy: Double, dz: Double) {
    val packet = PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE)

    packet.integers.write(0, entityId)
    packet.shorts
        .write(0, toFixedPoint12Bit(dx))
        .write(1, toFixedPoint12Bit(dy))
        .write(2, toFixedPoint12Bit(dz))

    ThePet.protocolManager.sendServerPacket(player, packet)
}

fun updateClientSideEntityVelocity(player: Player, entityId: Int, dx: Double, dy: Double, dz: Double) {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_VELOCITY)

    packet.integers
        .write(0, entityId)
        .write(1, convertVelocity(dx))
        .write(2, convertVelocity(dy))
        .write(3, convertVelocity(dz))

    ThePet.protocolManager.sendServerPacket(player, packet)
}

fun destroyClientSideEntity(player: Player, id: Int) {
    val packet = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)

    packet.modifier.write(0, IntArrayList(intArrayOf(id)))

    ThePet.protocolManager.sendServerPacket(player, packet)
}

fun setClientSideEntityMetadata(player: Player, entityId: Int, data: List<WrappedDataValue>) {
    val metadata = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)

    metadata.integers.write(0, entityId)
    metadata.dataValueCollectionModifier.write(0, data)

    ThePet.protocolManager.sendServerPacket(player, metadata)
}

private fun toFixedPoint12Bit(n: Double): Short = (n * (1 shl 12)).toInt().toShort()

private fun convertVelocity(velocity: Double): Int {
    return (clamp(velocity, -3.9, 3.9) * 8000).toInt()
}

private fun clamp(targetNum: Double, min: Double, max: Double): Double {
    // Makes sure a number is within a range
    return max(min, min(targetNum, max))
}

val BOOLEAN_SERIALIZER: Serializer = Registry.get(java.lang.Boolean::class.java)
val BYTE_SERIALIZER: Serializer = Registry.get(java.lang.Byte::class.java)
val INT_SERIALIZER: Serializer = Registry.get(java.lang.Integer::class.java)
val CHAT_SERIALIZER: Serializer = Registry.getChatComponentSerializer(false)
val CHAT_OPTIONAL_SERIALIZER: Serializer = Registry.getChatComponentSerializer(true)
val VECTOR_SERIALIZER: Serializer = Registry.get(org.joml.Vector3f::class.java)
