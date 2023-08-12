package me.znepb.zrm.block.signals

import me.znepb.zrm.block.PostMountableBlock
import me.znepb.zrm.block.entity.PostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

abstract class TrafficSignalBase<T : PostMountableBlockEntity>
    (settings: Settings, blockEntityFactory: BlockEntityType.BlockEntityFactory<T>)
    : PostMountableBlock<T>(settings, blockEntityFactory)
{
    fun beforeBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity = world.getBlockEntity(pos)

        if(blockEntity is TrafficSignalBlockEntityBase) {
            blockEntity.remove()
        }
    }
}