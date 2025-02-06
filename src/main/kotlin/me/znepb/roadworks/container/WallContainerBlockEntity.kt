package me.znepb.roadworks.container

import me.znepb.roadworks.RoadworksRegistry
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class WallContainerBlockEntity(pos: BlockPos, state: BlockState) : AttachmentContainerBlockEntity(RoadworksRegistry.ModBlockEntities.WALL_CONTAINER_BLOCK_ENTITY, pos, state) {
    companion object {
        fun onTick(world: World, pos: BlockPos, state: BlockState, blockEntity: WallContainerBlockEntity?) {
            blockEntity?.onTick(world)
        }
    }

    fun onTick(world: World) {
        this.setWorld(world)
        this.attachments.forEach { it.onTick() }
    }

    override fun getDepthOffset(): Double {
        return 0.5
    }
}