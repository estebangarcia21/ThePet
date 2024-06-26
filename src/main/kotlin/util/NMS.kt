package org.bluehats.util

import org.bluehats.ThePet
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

fun nbtKey(key: String): NamespacedKey = NamespacedKey(JavaPlugin.getProvidingPlugin(ThePet::class.java), key)

fun setNBTFloat(item: ItemStack, key: String, value: Float) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.FLOAT, value)

    item.itemMeta = meta
}

fun setNBTDouble(item: ItemStack, key: String, value: Double) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.DOUBLE, value)
    item.itemMeta = meta
}

fun setNBTInt(item: ItemStack, key: String, value: Int) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.INTEGER, value)
    item.itemMeta = meta
}

fun setNBTString(item: ItemStack, key: String, value: String) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.STRING, value)
    item.itemMeta = meta
}

fun setNBTByte(item: ItemStack, key: String, value: Byte) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.BYTE, value)
    item.itemMeta = meta
}

fun setNBTByteArray(item: ItemStack, key: String, value: ByteArray) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.BYTE_ARRAY, value)
    item.itemMeta = meta
}

fun getNBTFloat(item: ItemStack, key: String): Float? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.FLOAT)
}

fun getNBTDouble(item: ItemStack, key: String): Double? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.DOUBLE)
}

fun getNBTInt(item: ItemStack, key: String): Int? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.INTEGER)
}

fun getNBTString(item: ItemStack, key: String): String? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.STRING)
}

fun getNBTByte(item: ItemStack, key: String): Byte? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.BYTE)
}

fun getNBTByteArray(item: ItemStack, key: String): ByteArray? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.BYTE_ARRAY)
}

fun setNBTBoolean(item: ItemStack, key: String, value: Boolean) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.set(namespacedKey, PersistentDataType.BYTE, if (value) 1.toByte() else 0.toByte())
    item.itemMeta = meta
}

fun getNBTBoolean(item: ItemStack, key: String): Boolean? {
    val meta = item.itemMeta ?: return null
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.get(namespacedKey, PersistentDataType.BYTE)?.toInt() == 1
}

fun hasNBTEntryFor(item: ItemStack, key: String): Boolean {
    val meta = item.itemMeta ?: return false
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    return container.has(namespacedKey, PersistentDataType.BYTE)
}

fun removeNBTTag(item: ItemStack, key: String) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val namespacedKey = nbtKey(key)
    container.remove(namespacedKey)
    item.itemMeta = meta
}

fun getAllNBTTags(item: ItemStack): Map<String, Any> {
    val meta = item.itemMeta ?: return emptyMap()
    val container = meta.persistentDataContainer
    val keys = container.keys

    val nbtMap = mutableMapOf<String, Any>()
    val plugin = JavaPlugin.getProvidingPlugin(ThePet::class.java)

    for (key in keys) {
        val namespacedKey = NamespacedKey(plugin, key.key)
        when (key.namespace) {
            "string" -> container.get(namespacedKey, PersistentDataType.STRING)?.let { nbtMap[key.key] = it }
            "integer" -> container.get(namespacedKey, PersistentDataType.INTEGER)?.let { nbtMap[key.key] = it }
            "byte" -> container.get(namespacedKey, PersistentDataType.BYTE)?.let { nbtMap[key.key] = it }
            "long" -> container.get(namespacedKey, PersistentDataType.LONG)?.let { nbtMap[key.key] = it }
            "float" -> container.get(namespacedKey, PersistentDataType.FLOAT)?.let { nbtMap[key.key] = it }
            "double" -> container.get(namespacedKey, PersistentDataType.DOUBLE)?.let { nbtMap[key.key] = it }
            "byte_array" -> container.get(namespacedKey, PersistentDataType.BYTE_ARRAY)?.let { nbtMap[key.key] = it }
            "integer_array" -> container.get(namespacedKey, PersistentDataType.INTEGER_ARRAY)?.let { nbtMap[key.key] = it }
            "long_array" -> container.get(namespacedKey, PersistentDataType.LONG_ARRAY)?.let { nbtMap[key.key] = it }
            else -> {}
        }
    }

    return nbtMap
}

fun setNBTTagsFromMap(item: ItemStack, dataMap: Map<String, Any>) {
    val meta = item.itemMeta ?: return
    val container = meta.persistentDataContainer
    val plugin = JavaPlugin.getProvidingPlugin(ThePet::class.java)

    for ((key, value) in dataMap) {
        val namespacedKey = NamespacedKey(plugin, key)
        when (value) {
            is String -> container.set(namespacedKey, PersistentDataType.STRING, value)
            is Int -> container.set(namespacedKey, PersistentDataType.INTEGER, value)
            is Byte -> container.set(namespacedKey, PersistentDataType.BYTE, value)
            is Long -> container.set(namespacedKey, PersistentDataType.LONG, value)
            is Float -> container.set(namespacedKey, PersistentDataType.FLOAT, value)
            is Double -> container.set(namespacedKey, PersistentDataType.DOUBLE, value)
            is ByteArray -> container.set(namespacedKey, PersistentDataType.BYTE_ARRAY, value)
            is IntArray -> container.set(namespacedKey, PersistentDataType.INTEGER_ARRAY, value)
            is LongArray -> container.set(namespacedKey, PersistentDataType.LONG_ARRAY, value)
            else -> throw IllegalArgumentException("Unsupported NBT data type: ${value::class.java}")
        }
    }

    item.itemMeta = meta
}

//
//typealias DeserializedNBTMap = Map<String, Any>
//
//fun buildNBTCompound(map: DeserializedNBTMap): NBTTagCompound {
//    val compound = NBTTagCompound()
//
//    map.entries.forEach { (key, value) ->
//        when (value) {
//            is Byte -> compound.setByte(key, value)
//            is Short -> compound.setShort(key, value)
//            is Int -> compound.setInt(key, value)
//            is Long -> compound.setLong(key, value)
//            is Float -> compound.setFloat(key, value)
//            is Double -> compound.setDouble(key, value)
//            is String -> compound.setString(key, value)
//            is ByteArray -> compound.setByteArray(key, value)
//            is Boolean -> compound.setBoolean(key, value)
//            is NBTTagCompound -> compound.set(key, value)
//            else -> throw IllegalArgumentException("Unsupported NBT data type: ${value.javaClass}")
//        }
//    }
//
//    return compound
//}
//
///**
// * Merges the `compound` into the `target` `NBTTagCompound`.
// *
// * All entries from `compound` will override duplicate entries from `target`.
// */
//fun mergeNBTCompounds(target: NBTTagCompound?, compound: NBTTagCompound?): NBTTagCompound {
//    val mergedCompound = target ?: compound ?: NBTTagCompound()
//
//    if (compound != null) {
//        for (key in compound.keys) {
//            val value = compound.get(key)
//            mergedCompound.set(key, value)
//        }
//    }
//
//    return mergedCompound
//}
//
//fun readNBTCompoundAsMap(compound: NBTTagCompound?): DeserializedNBTMap {
//    if (compound == null) return emptyMap()
//
//    val map = mutableMapOf<String, Any>()
//
//    compound.keys.forEach { key ->
//        val result: Any = when (val value = compound.get(key)) {
//            is NBTTagString -> value.a_()
//            is NBTTagInt -> value.d()
//            is NBTTagFloat -> value.h()
//            is NBTTagLong -> value.c()
//            is NBTTagDouble -> value.g()
//            is NBTTagByte -> value.f()
//            is NBTTagByteArray -> value.c()
//            is NBTTagCompound -> readNBTCompoundAsMap(value)
//            else -> return@forEach
//        }
//
//        map[key] = result
//    }
//
//    return map
//}
//
//val NBTTagCompound.keys: List<String>
//    get() = this.c().toList()
//
//var ItemStack?.nbt: NBTTagCompound?
//    get() {
//        if (this == null) return null
//        val nmsItemStack = CraftItemStack.asNMSCopy(this) ?: return null
//
//        return nmsItemStack.tag ?: return null
//    }
//    set(tag) {
//        if (this == null) return
//        val nmsItemStack = CraftItemStack.asNMSCopy(this) ?: return
//
//        nmsItemStack.tag = tag
//
//        this.itemMeta = CraftItemStack.getItemMeta(nmsItemStack)
//    }
