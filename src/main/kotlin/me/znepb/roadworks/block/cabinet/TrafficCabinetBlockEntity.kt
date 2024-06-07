package me.znepb.roadworks.block.cabinet

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.block.Linkable
import me.znepb.roadworks.block.PedestrianButtonBlockEntity
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.roadworks.block.signals.SignalLight
import me.znepb.roadworks.block.signals.SignalType
import me.znepb.roadworks.util.MiscUtils.Companion.blockPosFromNbtIntArray
import me.znepb.roadworks.util.MiscUtils.Companion.blockPosToNbtIntArray
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TrafficCabinetBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(Registry.ModBlockEntities.CABINET_BLOCK_ENTITY, pos, state) {
    val peripheral = TrafficCabinetPeripheral(this)
    private var connections = Connections()
    private val idTypeCache = HashMap<Int, String>()
    private var signalSetQueue = HashMap<Int, HashMap<SignalLight, Boolean>>()

    fun getTotalDevices() = connections.getAmount()
    fun getConnections() = connections
    fun getTypeOfId(id: Int): String? {
        return idTypeCache[id]
    }

    fun getConnectionBlockEntityFromID(id: Int): BlockEntity? {
        val result = connections.get(id) ?: return null

        if(world != null) {
            val blockPos = result.getPos()
            if(world!!.isChunkLoaded(blockPos.x / 16, blockPos.z / 16)) {
                val entity = world!!.getBlockEntity(blockPos)

                return if(entity != null) {
                    entity
                } else {
                    // Entity disappeared!
                    removeConnection(id)
                    logger.warn("Connection with ID $id was removed incorrectly, removing now")
                    null
                }
            }
        }

        return null
    }

    fun getConnectionIdentifierFromBlockPos(pos: BlockPos): Int? {
        return connections.get(pos)?.getId()
    }

    fun addDevice(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is AbstractTrafficSignalBlockEntity) return addSignal(pos)
        else if(blockEntity is PedestrianButtonBlockEntity) return addButton(pos)
        else return null
    }

    fun addSignal(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is AbstractTrafficSignalBlockEntity) {
            val newSignal = connections.add(pos)
            blockEntity.getSignalType().lights.forEach {
                blockEntity.setSignal(it, it.genericType == SignalLight.RED || it.isGeneric && it == SignalLight.RED)
            }
            idTypeCache[newSignal.getId()] = blockEntity.getLinkType()
            this.markDirty()
            return newSignal.getId()
        }

        return null
    }

    fun addButton(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is PedestrianButtonBlockEntity) {
            val newButton = connections.add(pos)
            idTypeCache[newButton.getId()] = blockEntity.getLinkType()
            this.markDirty()
            return newButton.getId()
        }

        return null
    }

    fun removeConnection(pos: BlockPos) {
        getConnectionIdentifierFromBlockPos(pos)?.let { removeConnection(it) }
    }

    fun removeConnection(identifier: Int) {
        val connection = connections.get(identifier) ?: return

        val blockEntity = world?.getBlockEntity(connection.getPos())
        if(blockEntity is Linkable) {
            blockEntity.unlink()
        }
        connections.remove(connection.getId())

        this.markDirty()
    }

    fun remove() {
        connections.getAll().forEach {
            val signal = connections.get(it.getId()) ?: return

            val blockEntity = world?.getBlockEntity(signal.getPos())
            if(blockEntity is AbstractTrafficSignalBlockEntity) {
                blockEntity.unlink()
            }
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        connections.fromNbtList(nbt.get("connections") as NbtList)
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("connections", connections.toNbtList())

        super.writeNbt(nbt)
    }

    fun queueSignalSet(id: Int, signalLight: SignalLight, value: Boolean) {
        val idQueue = signalSetQueue.getOrPut(id) { hashMapOf() }
        idQueue[signalLight] = value
    }

    fun onTick(world: World, pos: BlockPos, state: BlockState) {
        if(signalSetQueue.isNotEmpty()) {
            connections.getAll().forEach {
                val item = signalSetQueue[it.getId()]

                if(item != null) {
                    val type = getTypeOfId(it.getId())
                    val signalBlockEntity = getConnectionBlockEntityFromID(it.getId())

                    if(signalBlockEntity is AbstractTrafficSignalBlockEntity) {
                        type?.let { it1 -> SignalType.fromType(it1) }!!.lights.forEach { light ->
                            if (item[light] != null) {
                                signalBlockEntity.queueSignalSet(light, item[light]!!)
                            }
                        }
                    }
                }
            }
        }

        this.connections.getAll().forEach {
            if(this.idTypeCache[it.getId()] == null) {
                try {
                    val blockEntity = this.world?.getBlockEntity(it.getPos())

                    if(blockEntity is Linkable) {
                        this.idTypeCache[it.getId()] = blockEntity.getLinkType()
                    }
                } catch(_: Exception) {}
            }
        }
    }

    class Connections {
        class Connection(private val id: Int, private val pos: BlockPos) {
            fun getId() = id
            fun getPos() = pos

            fun toNbt(): NbtCompound {
                val compound = NbtCompound()
                compound.putInt("id", id)
                compound.putIntArray("pos", blockPosToNbtIntArray(pos))
                return compound
            }

            companion object {
                fun fromNbt(nbt: NbtCompound): Connection {
                    return Connection(nbt.getInt("id"), blockPosFromNbtIntArray(nbt.getIntArray("pos")))
                }
            }
        }

        private val list = mutableListOf<Connection>()

        fun getNextId(): Int {
            var id = 1
            while(get(id) != null) {
                id++
            }

            return id
        }

        fun add(id: Int, pos: BlockPos): Connection {
            if(get(id) != null) throw IllegalArgumentException("ID $id already exists")
            if(get(pos) != null) throw IllegalArgumentException("Signal at $pos is already connected")
            val newConnection = Connection(id, pos)
            list.add(newConnection)
            return newConnection
        }

        fun add(pos: BlockPos): Connection {
            return this.add(getNextId(), pos)
        }

        fun get(id: Int): Connection? {
            val filtered = list.filter { it.getId() == id }
            return if(filtered.isNotEmpty()) filtered[0] else null
        }

        fun get(blockPos: BlockPos): Connection? {
            val filtered = list.filter { it.getPos() == blockPos }
            return if(filtered.isNotEmpty()) filtered[0] else null
        }

        fun remove(id: Int) {
            list.remove(list.filter { it.getId() == id }[0])
        }

        fun fromNbtList(list: NbtList) {
            list.forEach {
                if(
                    it is NbtCompound
                    && it.getType("pos") == NbtCompound.INT_ARRAY_TYPE
                    && it.getType("id") == NbtCompound.INT_TYPE
                ) {
                    this.list.add(Connection.fromNbt(it))
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

        fun getAll() = list
        fun getAmount() = list.size
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: TrafficCabinetBlockEntity) {
            blockEntity.onTick(world, pos, state)
        }
    }
}