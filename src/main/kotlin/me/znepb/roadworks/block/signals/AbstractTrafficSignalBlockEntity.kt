package me.znepb.roadworks.block.signals

import me.znepb.roadworks.block.Linkable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class AbstractTrafficSignalBlockEntity
    (
        pos: BlockPos,
        state: BlockState,
        blockEntityType: BlockEntityType<*>,
        private val signalType: SignalType
    )
    : Linkable(pos, state, blockEntityType)
{
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
    fun getLights() = signalType.lights

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

        super.readExtraNBT(nbt)
    }

    override fun writeExtraNBT(nbt: NbtCompound) {
        signalType.lights.forEach {
            signalState[it]?.let { it1 -> nbt.putBoolean(it.light, it1) }
        }

        super.writeExtraNBT(nbt)
    }

    override fun getLinkType() = this.getSignalType().type

    fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: AbstractTrafficSignalBlockEntity) {
        if(!world.isClient && !blockEntity.isLinked()) {
            val ticks = world.server?.ticks
            if (ticks != null) {
                SignalLight.getReds(state.block).forEach { queueSignalSet(it, (ticks % 40) > 20) }
                SignalLight.getGreens(state.block).forEach { queueSignalSet(it, false) }
                SignalLight.getYellows(state.block).forEach { queueSignalSet(it, false) }
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