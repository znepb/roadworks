package me.znepb.zrm.block.cabinet

import dan200.computercraft.api.lua.ObjectLuaTable
import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.block.signals.SignalType
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.zrm.util.MiscUtils.Companion.blockPosFromNbtIntArray
import me.znepb.zrm.util.MiscUtils.Companion.blockPosToNbtIntArray
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.LightType
import net.minecraft.world.World
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.sign

class TrafficCabinetBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(Registry.ModBlockEntities.CABINET_BLOCK_ENTITY, pos, state) {
    val peripheral = TrafficCabinetPeripheral(this)
    private var signals = SignalConnections()
    private val idTypeCache = HashMap<Int, SignalType>()
    private var queue = HashMap<Int, HashMap<SignalLight, Boolean>>()

    fun getSignals() = signals
    fun getTotalSignals(): Int = signals.getAmount()
    fun getTypeOfId(id: Int): SignalType? {
        return idTypeCache[id]
    }

    fun getSignalBlockEntityFromId(id: Int): BlockEntity? {
        val result = signals.getSignal(id) ?: return null

        if(world != null) {
            val blockPos = result.getPos()
            if(world!!.isChunkLoaded(blockPos.x / 16, blockPos.z / 16)) {
                val entity = world!!.getBlockEntity(blockPos)

                return if(entity != null) {
                    entity
                } else {
                    // Entity disappeared!
                    removeSignal(id)
                    logger.warn("Signal with ID $id was removed incorrectly, removing now")
                    null
                }
            }
        }

        return null
    }

    fun getSignalIdentifierFromBlockPos(pos: BlockPos): Int? {
        return signals.getSignal(pos)?.getId()
    }

    fun addSignal(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is AbstractTrafficSignalBlockEntity) {
            val newSignal = signals.addSignal(pos)
            blockEntity.getSignalType().lights.forEach {
                blockEntity.setSignal(it, it.genericType == SignalLight.RED || it.isGeneric && it == SignalLight.RED)
            }
            idTypeCache[newSignal.getId()] = blockEntity.getSignalType()
            this.markDirty()
            return newSignal.getId()
        }

        return null
    }

    fun removeSignal(pos: BlockPos) {
        getSignalIdentifierFromBlockPos(pos)?.let { removeSignal(it) }
    }

    fun removeSignal(identifier: Int) {
        val signal = signals.getSignal(identifier) ?: return

        val blockEntity = world?.getBlockEntity(signal.getPos())
        if(blockEntity is AbstractTrafficSignalBlockEntity) {
            blockEntity.unlink()
        }
        signals.removeSignal(signal.getId())

        this.markDirty()
    }

    fun remove() {
        signals.getSignals().forEach {
            val signal = signals.getSignal(it.getId()) ?: return

            val blockEntity = world?.getBlockEntity(signal.getPos())
            if(blockEntity is AbstractTrafficSignalBlockEntity) {
                blockEntity.unlink()
            }
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        signals.fromNbtList(nbt.get("signals") as NbtList)
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("signals", signals.toNbtList())

        super.writeNbt(nbt)
    }

    fun queueSignalSet(id: Int, signalLight: SignalLight, value: Boolean) {
        val idQueue = queue.getOrPut(id) { hashMapOf() }
        idQueue[signalLight] = value
    }

    fun onTick(world: World, pos: BlockPos, state: BlockState) {
        if(queue.isNotEmpty()) {
            signals.getSignals().forEach {
                val item = queue[it.getId()]

                if(item != null) {
                    val type = getTypeOfId(it.getId())
                    val signalBlockEntity = getSignalBlockEntityFromId(it.getId())

                    if(signalBlockEntity is AbstractTrafficSignalBlockEntity) {
                        type?.lights?.forEach { light ->
                            if (item[light] != null) {
                                signalBlockEntity.queueSignalSet(light, item[light]!!)
                            }
                        }
                    }
                }
            }
        }

        this.signals.getSignals().forEach {
            if(this.idTypeCache[it.getId()] == null) {
                try {
                    val blockEntity = this.world?.getBlockEntity(it.getPos())

                    if(blockEntity is AbstractTrafficSignalBlockEntity) {
                        this.idTypeCache[it.getId()] = blockEntity.getSignalType()
                    }
                } catch(_: Exception) {}
            }
        }
    }

    class SignalConnections {
        class SignalConnection(private val id: Int, private val pos: BlockPos) {
            fun getId() = id
            fun getPos() = pos

            fun toNbt(): NbtCompound {
                val compound = NbtCompound()
                compound.putInt("id", id)
                compound.putIntArray("pos", blockPosToNbtIntArray(pos))
                return compound
            }

            companion object {
                fun fromNbt(nbt: NbtCompound): SignalConnection {
                    return SignalConnection(nbt.getInt("id"), blockPosFromNbtIntArray(nbt.getIntArray("pos")))
                }
            }
        }

        private val list = mutableListOf<SignalConnection>()

        fun getNextId(): Int {
            var id = 1
            while(getSignal(id) != null) {
                id++
            }

            return id
        }

        fun addSignal(id: Int, pos: BlockPos): SignalConnection {
            if(getSignal(id) != null) throw IllegalArgumentException("ID $id already exists")
            if(getSignal(pos) != null) throw IllegalArgumentException("Signal at $pos is already connected")
            val newConnection = SignalConnection(id, pos)
            list.add(newConnection)
            return newConnection
        }

        fun addSignal(pos: BlockPos): SignalConnection {
            return this.addSignal(getNextId(), pos)
        }

        fun getSignal(id: Int): SignalConnection? {
            val filtered = list.filter { it.getId() == id }
            return if(filtered.isNotEmpty()) filtered[0] else null
        }

        fun getSignal(blockPos: BlockPos): SignalConnection? {
            val filtered = list.filter { it.getPos() == blockPos }
            return if(filtered.isNotEmpty()) filtered[0] else null
        }

        fun removeSignal(id: Int) {
            list.remove(list.filter { it.getId() == id }[0])
        }

        fun fromNbtList(list: NbtList) {
            list.forEach {
                if(
                    it is NbtCompound
                    && it.getType("pos") == NbtCompound.INT_ARRAY_TYPE
                    && it.getType("id") == NbtCompound.INT_TYPE
                ) {
                    this.list.add(SignalConnection.fromNbt(it))
                }
            }
        }

        fun toNbtList(): NbtList {
            val list = NbtList()
            this.list.forEach {
                list.add(it.toNbt())
            }
            return list
        }

        fun getSignals() = list
        fun getAmount() = list.size
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: TrafficCabinetBlockEntity) {
            blockEntity.onTick(world, pos, state)
        }
    }
}