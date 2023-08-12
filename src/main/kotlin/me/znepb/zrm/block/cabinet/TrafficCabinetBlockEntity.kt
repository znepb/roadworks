package me.znepb.zrm.block.cabinet

import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.SignalLight
import me.znepb.zrm.block.signals.SignalType
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntityBase
import me.znepb.zrm.util.MiscUtils.Companion.blockPosFromNbtIntArray
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.util.math.BlockPos

class TrafficCabinetBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(Registry.ModBlockEntities.CABINET_BLOCK_ENTITY, pos, state) {
    val peripheral = TrafficCabinetPeripheral(this)
    private var signals = NbtList()
    private var nextID = 1
    private val idTypeCache = HashMap<Int, SignalType>()

    fun getSignals() = signals
    fun getTotalSignals(): Int = signals.size
    fun getIdType(id: Int): SignalType? {
        return idTypeCache[id]
    }

    fun getSignalBlockEntityFromId(id: Int): BlockEntity? {
        val result = signals.filter { it is NbtCompound && it.getInt("id") == id }
        if(result.isEmpty()) return null
        val element = result[0]

        if(element is NbtCompound && world != null) {
            val blockPos = blockPosFromNbtIntArray(element.getIntArray("position"))
            if(world!!.isChunkLoaded(blockPos.x / 16, blockPos.z / 16)) {
                val entity = world!!.getBlockEntity(blockPos)

                if(entity != null) {
                    return entity
                } else {
                    // Entity disappeared!
                    removeSignal(id)
                    logger.warn("Signal with ID $id was removed incorrectly, removing now")
                    return null
                }
            }
        }

        return null
    }

    fun getSignalIdentifierFromBlockPos(pos: BlockPos): Int? {
        signals.forEach {
            if (it is NbtCompound) {
                val list = it.getIntArray("position")
                if (list[0] == pos.x && list[1] == pos.y && list[2] == pos.z) {
                    return it.getInt("id")
                }
            }
        }

        return null
    }

    fun addSignal(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is AbstractTrafficSignalBlockEntityBase) {
            val data = NbtCompound()
            data.putInt("id", nextID)
            data.put("position", NbtIntArray(listOf(pos.x, pos.y, pos.z)))

            signals.add(data)
            blockEntity.setSignal(SignalLight.RED, false)
            blockEntity.setSignal(SignalLight.YELLOW, false)
            blockEntity.setSignal(SignalLight.GREEN, false)
            idTypeCache[nextID] = blockEntity.getSignalType()

            nextID += 1
            this.markDirty()

            return nextID - 1
        }

        return null
    }

    fun removeSignal(pos: BlockPos) {
        getSignalIdentifierFromBlockPos(pos)?.let { removeSignal(it) }
    }

    fun removeSignal(identifier: Int) {
        val result = signals.filter { it is NbtCompound && it.getInt("id") == identifier }
        val element = result[0]

        if(element is NbtCompound) {
            val blockEntity = world?.getBlockEntity(blockPosFromNbtIntArray(element.getIntArray("position")))
            if(blockEntity is AbstractTrafficSignalBlockEntityBase) {
                blockEntity.unlink()
            }
        }

        if(element != null) signals.remove(element)

        this.markDirty()
    }

    fun remove() {
        if(signals.size > 0) {
            for (i in 0 until signals.size) {
                val element = signals[0]

                if (element is NbtCompound) {
                    removeSignal(element.getInt("id"))
                }
            }
        }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        signals = if(nbt.contains("signals")) nbt.get("signals") as NbtList else NbtList()
        nextID = nbt.getInt("nextID")
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("signals", signals)
        nbt.putInt("nextID", nextID)

        super.writeNbt(nbt)
    }
}