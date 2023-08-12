package me.znepb.zrm.block.signals

import me.znepb.zrm.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractTrafficSignalBlockEntityBase
    (
        pos: BlockPos,
        state: BlockState,
        blockEntityType: BlockEntityType<*>,
        private val signalType: SignalType
    )
    : AbstractPostMountableBlockEntity(blockEntityType, pos, state)
{
    protected var linked = false
    protected var linkX = 0
    protected var linkY = 0
    protected var linkZ = 0
    protected val signalState = signalStateMapFromList()
    protected val queue = HashMap<SignalLight, Boolean>()

    init {
        this.markDirty()
    }

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractTrafficSignalBlockEntityBase) {
            blockEntity.onTick(world, pos, state, blockEntity)
        }
    }

    private fun signalStateMapFromList(): HashMap<SignalLight, Boolean> {
        val map = hashMapOf<SignalLight, Boolean>()
        signalType.lights.forEach { map[it] = false }

        return map
    }

    fun getSignalType() = signalType
    fun getLinkPos() = BlockPos(linkX, linkY, linkZ)
    fun isLinked() = linked
    fun getLights() = signalType.lights

    fun link(cabinetBlockEntity: TrafficCabinetBlockEntity): Int? {
        val id = cabinetBlockEntity.addSignal(this.pos)

        if(id != null) {
            linkX = cabinetBlockEntity.pos.x
            linkY = cabinetBlockEntity.pos.y
            linkZ = cabinetBlockEntity.pos.z
            linked = true
            this.markDirty()

            return id
        }

        return null
    }

    fun unlink() {
        linked = false
        reset()
        this.markDirty()
    }

    fun remove() {
        // Remove from cabinet when this block is removed
        if(linked) {
            val blockEntity = this.world?.getBlockEntity(getLinkPos())
            if(blockEntity is TrafficCabinetBlockEntity) {
                blockEntity.removeSignal(this.pos)
            }
        }
    }

    fun reset() {
        signalType.lights.forEach {
            signalState[it] = false
        }
        this.markDirty()
    }

    fun queueSignalSet(signalType: SignalLight, value: Boolean) {
        queue[signalType] = value
    }

    fun getSignal(signal: SignalLight): Boolean {
        return signalState[signal] == true
    }

    fun setSignal(signal: SignalLight, value: Boolean) {
        if (!signalType.lights.contains(signal)) return
        signalState[signal] = value
        this.markDirty()
    }

    override fun readExtraNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it] = nbt.getBoolean(it.name)
        }

        linked = nbt.getBoolean("linked")
        linkX = nbt.getInt("linkX")
        linkY = nbt.getInt("linkX")
        linkZ = nbt.getInt("linkX")
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it]?.let { it1 -> nbt.putBoolean(it.name, it1) }
        }

        nbt.putBoolean("linked", linked)
        nbt.putInt("linkX", linkX)
        nbt.putInt("linkY", linkY)
        nbt.putInt("linkZ", linkZ)
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener> {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound? {
        return createNbt()
    }

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractTrafficSignalBlockEntityBase) {
        if(!blockEntity.isLinked() && !world.isClient) {
            val ticks = world.server?.ticks
            if(ticks != null) {
                setSignal(SignalLight.RED, (ticks % 40) > 20);
            }
        }

        if(queue.size > 0) {
            queue.forEach {
                this.setSignal(it.key, it.value)
                queue.remove(it.key)
            }
        }

        super.onTick(world)
    }
}