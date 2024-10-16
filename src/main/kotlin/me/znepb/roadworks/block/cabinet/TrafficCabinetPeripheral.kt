package me.znepb.roadworks.block.cabinet

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.ObjectLuaTable
import dan200.computercraft.api.peripheral.IComputerAccess
import dan200.computercraft.api.peripheral.IPeripheral
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.signals.SignalLight
import me.znepb.roadworks.block.signals.SignalType

class TrafficCabinetPeripheral(val blockEntity: TrafficCabinetBlockEntity) : IPeripheral {
    override fun getType() = "traffic_cabinet"
    override fun getTarget() = blockEntity
    private val attachedComputers: MutableList<IComputerAccess> = mutableListOf()

    override fun attach(computer: IComputerAccess) {
        this.attachedComputers.add(computer)
        super.attach(computer)
    }

    override fun detach(computer: IComputerAccess) {
        this.attachedComputers.remove(computer)
        super.detach(computer)
    }

    fun notifyButtonPush(id: Int) {
        this.attachedComputers.forEach {
            it.queueEvent("cabinet_trigger", it.attachmentName, "pedestrian_button", id)
        }
    }

    /// Returns whether this traffic cabinet has the specified ID available.
    @LuaFunction
    fun hasId(id: Int): Boolean {
        blockEntity.getConnections().getAll().forEach {
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
    fun getType(id: Int): String? {
        if(!hasId(id)) return null

        blockEntity.getConnections().getAll().forEach {
            return blockEntity.getTypeOfId(it.getId())
        }

        return null
    }

    /// Gets signals of a type.
    @LuaFunction
    fun getConnectionsOfType(type: String): List<Int> {
        val list = mutableListOf<Int>()

        blockEntity.getConnections().getAll().forEach {
            if(blockEntity.getTypeOfId(it.getId()) == type) {
                list.add(it.getId())
            }
        }

        return list
    }

    /// Gets all signals on this traffic cabinet.
    @LuaFunction
    fun getConnections(): List<Any> {
        val signals = mutableListOf<ObjectLuaTable>()

        blockEntity.getConnections().getAll().forEach {
                val map = hashMapOf<Any, Any?>()
                map["id"] = it.getId()
                map["type"] = blockEntity.getTypeOfId(it.getId())

                signals.add(ObjectLuaTable(map))
        }

        return signals
    }

    /// Sets the balue of a beacon.
    @LuaFunction
    fun setBeacon(id: Int, on: Boolean): Boolean {
        return when (val type = blockEntity.getTypeOfId(id)?.let { SignalType.fromType(it) }) {
            SignalType.ONE_HEAD_RED -> {
                blockEntity.queueSignalSet(id, SignalLight.RED, on)
                true
            }

            SignalType.ONE_HEAD_YELLOW -> {
                blockEntity.queueSignalSet(id, SignalLight.YELLOW, on)
                true
            }

            SignalType.ONE_HEAD_GREEN-> {
                blockEntity.queueSignalSet(id, SignalLight.GREEN, on)
                true
            }
            else -> throw LuaException("invalid signal type, got $type")
        }
    }

    /// Sets the colors of a three-head signal.
    @LuaFunction
    fun setThreeHead(id: Int, red: Boolean, yellow: Boolean, green: Boolean): Boolean {
        when (val type = blockEntity.getTypeOfId(id)?.let { SignalType.fromType(it) }) {
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
            else -> throw LuaException("invalid signal type, got $type")
        }

    }

    @LuaFunction
    fun setFiveHead(id: Int, red: Boolean, yellowLeft: Boolean, greenLeft: Boolean, yellowRight: Boolean, greenRight: Boolean): Boolean {
        return when (val type = blockEntity.getTypeOfId(id)?.let { SignalType.fromType(it) }) {
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
            else -> throw LuaException("invalid signal type, got $type")
        }
    }

    /// Sets the state of a pedestrian signal.
    @LuaFunction
    fun setPedestrianSignal(id: Int, walk: Boolean): Boolean {
        return when (val type = blockEntity.getTypeOfId(id)?.let { SignalType.fromType(it) }) {
            SignalType.PEDESTRIAN -> {
                blockEntity.queueSignalSet(id, SignalLight.WALK, walk)
                blockEntity.queueSignalSet(id, SignalLight.DONT_WALK, !walk)
                true
            }
            else -> throw LuaException("invalid signal type, got $type")
        }
    }


    override fun equals(other: IPeripheral?): Boolean {
        return other is TrafficCabinetPeripheral
    }
}