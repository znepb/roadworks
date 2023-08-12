package me.znepb.zrm.block.cabinet

import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.LuaTable
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.lua.ObjectLuaTable
import dan200.computercraft.api.peripheral.IPeripheral
import me.znepb.zrm.Main.logger
import me.znepb.zrm.block.entity.signals.ThreeHeadTrafficSignalBlockEntity
import me.znepb.zrm.util.MiscUtils.Companion.blockPosFromNbtIntArray
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import org.spongepowered.asm.mixin.Mutable

class TrafficCabinetPeripheral(val blockEntity: TrafficCabinetBlockEntity) : IPeripheral {
    override fun getType() = "traffic_cabinet"
    override fun getTarget() = blockEntity

    fun getThreeHeadSignalBlockEntity(id: Int): ThreeHeadTrafficSignalBlockEntity? {
        val signal = blockEntity.getSignalBlockEntity(id)

        return if(signal is ThreeHeadTrafficSignalBlockEntity) {
            signal
        } else {
            null
        }
    }

    /// Returns whether this traffic cabinet has the specified ID available.
    @LuaFunction
    fun hasId(id: Int): Boolean {
        blockEntity.getThreeHeadSignals().forEach {
            if(it is NbtCompound) {
                if(it.getInt("id") == id) {
                    return true
                }
            }
        }

        return false
    }

    /// Returns whether a signal type has the specified light.
    @LuaFunction
    fun hasLight(type: String, light: String): Boolean {
        return when(type) {
            "threeHead" -> light == "red" || light == "green" || light == "yellow"
            else -> false
        }
    }

    /// Gets the signal's type from its ID.
    @LuaFunction
    fun getSignalType(id: Int): String? {
        if(!hasId(id)) return null

        blockEntity.getThreeHeadSignals().forEach {
            if(it is NbtCompound) {
                if(it.getInt("id") == id) {
                    return "threeHead"
                }
            }
        }

        return null
    }

    /// Gets signals of a type.
    @LuaFunction
    fun getSignalsOfType(type: String): List<Int> {
        val list = mutableListOf<Int>()

        blockEntity.getThreeHeadSignals().forEach { if(it is NbtCompound) { list.add(it.getInt("id")) } }

        return list
    }

    /// Gets all signals on this traffic cabinet.
    @LuaFunction
    fun getSignals(): List<Any> {
        val signals = mutableListOf<ObjectLuaTable>();

        blockEntity.getThreeHeadSignals().forEach {
            if(it is NbtCompound) {
                val map = hashMapOf<Any, Any>()
                map["id"] = it.getInt("id")
                map["type"] = "threeHead"

                signals.add(ObjectLuaTable(map))
            }
        }

        return signals
    }

    /// Sets the colors of a three-head signal.
    @LuaFunction(mainThread = true)
    fun setThreeHead(id: Int, red: Boolean, yellow: Boolean, green: Boolean): Boolean {
        val entity = getThreeHeadSignalBlockEntity(id)
        return if(entity != null) {
            entity.setRed(red)
            entity.setYellow(yellow)
            entity.setGreen(green)
            true
        } else {
            false
        }
    }

    override fun equals(other: IPeripheral?): Boolean {
        return other is TrafficCabinetPeripheral
    }
}