package me.znepb.zrm.block.cabinet

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.ObjectLuaTable
import dan200.computercraft.api.peripheral.IPeripheral
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.block.signals.SignalType
import me.znepb.zrm.block.signals.TrafficSignalBlockEntityBase
import net.minecraft.nbt.NbtCompound

class TrafficCabinetPeripheral(val blockEntity: TrafficCabinetBlockEntity) : IPeripheral {
    override fun getType() = "traffic_cabinet"
    override fun getTarget() = blockEntity

    fun getSignalBlockEntity(id: Int): TrafficSignalBlockEntityBase? {
        val signal = blockEntity.getSignalBlockEntityFromId(id)
        return if(signal is TrafficSignalBlockEntityBase) signal else null
    }

    /// Returns whether this traffic cabinet has the specified ID available.
    @LuaFunction
    fun hasId(id: Int): Boolean {
        blockEntity.getSignals().forEach {
            if(it is NbtCompound && it.getInt("id") == id) {
                return true
            }
        }

        return false
    }

    /// Returns whether a signal type has the specified light.
    @LuaFunction
    fun hasLight(type: String, light: String): Boolean {
        val info = SignalType.fromType(type)
        return info != null && info.lights.contains(SignalLight.fromName(light))
    }

    /// Gets the signal's type from its ID.
    @LuaFunction
    fun getSignalType(id: Int): String? {
        if(!hasId(id)) return null

        blockEntity.getSignals().forEach {
            if(it is NbtCompound) {
                return blockEntity.getIdType(it.getInt("id"))?.type
            }
        }

        return null
    }

    /// Gets signals of a type.
    @LuaFunction
    fun getSignalsOfType(type: String): List<Int> {
        val list = mutableListOf<Int>()

        blockEntity.getSignals().forEach {
            if(it is NbtCompound && blockEntity.getIdType(it.getInt("id"))?.type == type) {
                list.add(it.getInt("id"))
            }
        }

        return list
    }

    /// Gets all signals on this traffic cabinet.
    @LuaFunction
    fun getSignals(): List<Any> {
        val signals = mutableListOf<ObjectLuaTable>();

        blockEntity.getSignals().forEach {
            if(it is NbtCompound) {
                val map = hashMapOf<Any, Any?>()
                map["id"] = it.getInt("id")
                map["type"] = blockEntity.getIdType(it.getInt("id"))?.type

                signals.add(ObjectLuaTable(map))
            }
        }

        return signals
    }

    /// Sets the colors of a three-head signal.
    @LuaFunction
    fun setThreeHead(id: Int, red: Boolean, yellow: Boolean, green: Boolean): Boolean {
        val entity = getSignalBlockEntity(id)

        if(entity?.getSignalType() == SignalType.THREE_HEAD) {
            entity.queueSignalSet(SignalLight.RED, red)
            entity.queueSignalSet(SignalLight.YELLOW, yellow)
            entity.queueSignalSet(SignalLight.GREEN, green)

            return true
        }

        return false
    }

    override fun equals(other: IPeripheral?): Boolean {
        return other is TrafficCabinetPeripheral
    }
}