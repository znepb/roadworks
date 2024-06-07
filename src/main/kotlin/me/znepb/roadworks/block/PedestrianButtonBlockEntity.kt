package me.znepb.roadworks.block

import me.znepb.roadworks.Registry.ModBlockEntities.PEDESTRIAN_BUTTON_BLOCK_ENTITY
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class PedestrianButtonBlockEntity(
    pos: BlockPos,
    state: BlockState,
) : Linkable(pos, state, PEDESTRIAN_BUTTON_BLOCK_ENTITY) {
    init {
        this.markDirty()
    }

    fun press() {
        val id = this.getLinkedCabinet()?.getConnectionIdentifierFromBlockPos(this.pos)
        id?.let { this.getLinkedCabinet()?.peripheral?.notifyButtonPush(it) }
    }

    override fun getLinkType(): String = "button"

    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: PedestrianButtonBlockEntity) {
            blockEntity.onTick(world, pos, state, blockEntity)
        }
    }
}