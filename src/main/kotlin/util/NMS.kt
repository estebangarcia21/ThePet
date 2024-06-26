package org.bluehats.util

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.WrappedDataValue
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer
import org.bluehats.ThePet
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.util.*

/**
 * @return The client-side entity ID.
 */
fun spawnClientSideEntity(player: Player, location: Location, type: EntityType): Int {
    val entityId = (0..Short.MAX_VALUE).random()
    val packet = PacketContainer(PacketType.Play.Server.SPAWN_ENTITY)

    packet.integers.write(0, entityId)
    packet.uuiDs.write(0, UUID.randomUUID())
    packet.entityTypeModifier.write(0, type);

    packet.doubles
        .write(0, location.x)
        .write(1, location.y)
        .write(2, location.z);

//    packet.integers
//        .write(1, convertVelocity(vector.getX()))
//        .write(2, convertVelocity(vector.getY()))
//        .write(3, convertVelocity(vector.getZ()));

    ThePet.protocolManager.sendServerPacket(player, packet)

    return entityId
}


fun setClientSideEntityMetadata(entityId: Int, data: List<WrappedDataValue>) {
    val metadata = PacketContainer(PacketType.Play.Server.ENTITY_METADATA)

    metadata.integers.write(0, entityId)
    metadata.dataValueCollectionModifier.write(0, data)

    ThePet.protocolManager.broadcastServerPacket(metadata)
}

fun <S : Serializer, V> wrappedDataValue(index: Int, clazz: S, value: V): WrappedDataValue =
    WrappedDataValue(index, clazz, value)
