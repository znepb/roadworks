package me.znepb.zrm.block.cabinet

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.ObjectLuaTable
import dan200.computercraft.api.peripheral.IPeripheral
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.block.signals.SignalType
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import net.minecraft.nbt.NbtCompound

class TrafficCabinetPeripheral(val blockEntity: TrafficCabinetBlockEntity) : IPeripheral {
    override fun getType() = "traffic_cabinet"
    override fun getTarget() = blockEntity

    fun getSignalBlockEntity(id: Int): AbstractTrafficSignalBlockEntity? {
        val signal = blockEntity.getSignalBlockEntityFromId(id)
        return if(signal is AbstractTrafficSignalBlockEntity) signal else null
    }

    /// Returns whether this traffic cabinet has the specified ID available.
    @LuaFunction
    fun hasId(id: Int): Boolean {
        blockEntity.getSignals().getSignals().forEach {
            if(it.getId() == id) {
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

        blockEntity.getSignals().getSignals().forEach {
            return blockEntity.getTypeOfId(it.getId())?.type
        }

        return null
    }

    /// Gets signals of a type.
    @LuaFunction
    fun getSignalsOfType(type: String): List<Int> {
        val list = mutableListOf<Int>()

        blockEntity.getSignals().getSignals().forEach {
            if(blockEntity.getTypeOfId(it.getId())?.type == type) {
                list.add(it.getId())
            }
        }

        return list
    }

    /// Gets all signals on this traffic cabinet.
    @LuaFunction
    fun getSignals(): List<Any> {
        val signals = mutableListOf<ObjectLuaTable>();

        blockEntity.getSignals().getSignals().forEach {
                val map = hashMapOf<Any, Any?>()
                map["id"] = it.getId()
                map["type"] = blockEntity.getTypeOfId(it.getId())?.type

                signals.add(ObjectLuaTable(map))
        }

        return signals
    }

    /// Sets the colors of a three-head signal.
    @LuaFunction
    fun setThreeHead(id: Int, red: Boolean, yellow: Boolean, green: Boolean): Boolean {
        val type = blockEntity.getTypeOfId(id)

        when (type) {
            SignalType.THREE_HEAD -> {
                blockEntity.queueSignalSet(id, SignalLight.RED, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW, yellow)
                blockEntity.queueSignalSet(id, SignalLight.GREEN, green)
                return true
            }
            SignalType.THREE_HEAD_LEFT -> {
                blockEntity.queueSignalSet(id, SignalLight.RED_LEFT, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW_LEFT, yellow)
                blockEntity.queueSignalSet(id, SignalLight.GREEN_LEFT, green)
                return true
            }
            SignalType.THREE_HEAD_STRAIGHT -> {
                blockEntity.queueSignalSet(id, SignalLight.RED_STRAIGHT, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW_STRAIGHT, yellow)
                blockEntity.queueSignalSet(id, SignalLight.GREEN_STRAIGHT, green)
                return true
            }
            SignalType.THREE_HEAD_RIGHT -> {
                blockEntity.queueSignalSet(id, SignalLight.RED_RIGHT, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW_RIGHT, yellow)
                blockEntity.queueSignalSet(id, SignalLight.GREEN_RIGHT, green)
                return true
            }
            else -> return false
        }

    }

    @LuaFunction
    fun setFiveHead(id: Int, red: Boolean, yellowLeft: Boolean, greenLeft: Boolean, yellowRight: Boolean, greenRight: Boolean): Boolean {
        val type = blockEntity.getTypeOfId(id)

        return when (type) {
            SignalType.FIVE_HEAD_LEFT -> {
                blockEntity.queueSignalSet(id, SignalLight.RED, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW, yellowRight)
                blockEntity.queueSignalSet(id, SignalLight.GREEN, greenRight)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW_LEFT, yellowLeft)
                blockEntity.queueSignalSet(id, SignalLight.GREEN_LEFT, greenLeft)
                true
            }
            SignalType.FIVE_HEAD_RIGHT -> {
                blockEntity.queueSignalSet(id, SignalLight.RED, red)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW, yellowLeft)
                blockEntity.queueSignalSet(id, SignalLight.GREEN, greenLeft)
                blockEntity.queueSignalSet(id, SignalLight.YELLOW_RIGHT, yellowRight)
                blockEntity.queueSignalSet(id, SignalLight.GREEN_RIGHT, greenRight)
                true
            }
            else -> false
        }

    }

    override fun equals(other: IPeripheral?): Boolean {
        return other is TrafficCabinetPeripheral
    }
}