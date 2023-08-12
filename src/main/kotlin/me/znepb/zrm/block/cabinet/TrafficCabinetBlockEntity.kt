package me.znepb.zrm.block.cabinet

import dan200.computercraft.api.peripheral.IPeripheral
import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.block.entity.signals.ThreeHeadTrafficSignalBlockEntity
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
    private var threeHeadSignals = NbtList()
    private var nextID = 1

    fun getThreeHeadSignals() = threeHeadSignals
    fun getTotalSignalCount(): Int {
        return threeHeadSignals.size
    }

    fun getSignalBlockEntity(id: Int): BlockEntity? {
        val result = threeHeadSignals.filter { it is NbtCompound && it.getInt("id") == id }
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
                    removeThreeHeadSignal(id)
                    logger.warn("Signal with ID $id was removed incorrectly, removing now")
                    return null
                }
            }
        }

        return null
    }

    fun getIdentifierOfThreeHeadSignal(pos: BlockPos): Int? {
        threeHeadSignals.forEach {
            if (it is NbtCompound) {
                val list = it.getIntArray("position")
                if (list[0] == pos.x && list[1] == pos.y && list[2] == pos.z) {
                    return it.getInt("id")
                }
            }
        }

        return null
    }

    fun addThreeHeadSignal(pos: BlockPos): Int? {
        val blockEntity = this.world?.getBlockEntity(pos)

        if(blockEntity is ThreeHeadTrafficSignalBlockEntity) {
            val data = NbtCompound()
            data.putInt("id", nextID)
            data.put("position", NbtIntArray(listOf(pos.x, pos.y, pos.z)))
            threeHeadSignals.add(data)

            blockEntity.setRed(true)
            blockEntity.setYellow(false)
            blockEntity.setGreen(false)

            nextID += 1
            this.markDirty()

            return nextID - 1
        }

        return null
    }

    fun removeThreeHeadSignal(pos: BlockPos) {
        getIdentifierOfThreeHeadSignal(pos)?.let { removeThreeHeadSignal(it) }
    }

    fun removeThreeHeadSignal(identifier: Int) {
        val result = threeHeadSignals.filter { it is NbtCompound && it.getInt("id") == identifier }
        val element = result[0]

        if(element is NbtCompound) {
            val blockEntity = world?.getBlockEntity(blockPosFromNbtIntArray(element.getIntArray("position")))
            if(blockEntity is ThreeHeadTrafficSignalBlockEntity) {
                blockEntity.unlink()
            }
        }

        if(element != null) threeHeadSignals.remove(element)

        this.markDirty()
    }

    fun peripheral(): IPeripheral {
        return peripheral
    }

    fun remove() {
        if(threeHeadSignals.size > 0) {
            for (i in 0 until threeHeadSignals.size) {
                val element = threeHeadSignals[0]

                if (element is NbtCompound) {
                    removeThreeHeadSignal(element.getInt("id"))
                }
            }
        }

        super.markRemoved()
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        threeHeadSignals = if(nbt.contains("threeHeadSignals")) nbt.get("threeHeadSignals") as NbtList else NbtList()
        nextID = nbt.getInt("nextID")
        this.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("threeHeadSignals", threeHeadSignals)
        nbt.putInt("nextID", nextID)

        super.writeNbt(nbt)
    }
}