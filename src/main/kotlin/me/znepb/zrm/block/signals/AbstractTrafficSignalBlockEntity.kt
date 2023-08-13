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

abstract class AbstractTrafficSignalBlockEntity
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
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractTrafficSignalBlockEntity) {
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

            SignalLight.getReds(cachedState.block).forEach { queueSignalSet(it, true) }
            SignalLight.getGreens(cachedState.block).forEach { queueSignalSet(it, false) }
            SignalLight.getYellows(cachedState.block).forEach { queueSignalSet(it, false) }

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

    private fun reset() {
        this.linked = false
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
            signalState[it] = nbt.getBoolean(it.light)
        }

        linked = nbt.getBoolean("linked")
        linkX = nbt.getInt("linkX")
        linkY = nbt.getInt("linkY")
        linkZ = nbt.getInt("linkZ")
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it]?.let { it1 -> nbt.putBoolean(it.light, it1) }
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

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractTrafficSignalBlockEntity) {
        if(!world.isClient) {
            if (!blockEntity.isLinked()) {
                val ticks = world.server?.ticks
                if (ticks != null) {
                    SignalLight.getReds(state.block).forEach { queueSignalSet(it, (ticks % 40) > 20) }
                    SignalLight.getGreens(state.block).forEach { queueSignalSet(it, false) }
                    SignalLight.getYellows(state.block).forEach { queueSignalSet(it, false) }
                }
            } else if (world.getBlockEntity(BlockPos(this.linkX, this.linkY, this.linkZ)) !is TrafficCabinetBlockEntity) {
                this.unlink()
            }
        }

        if(queue.size > 0) {
            signalType.lights.forEach {
                if(queue[it] != null) {
                    val value = queue[it]!!
                    this.setSignal(it, value)
                }
            }
        }

        super.onTick(world)
    }
}