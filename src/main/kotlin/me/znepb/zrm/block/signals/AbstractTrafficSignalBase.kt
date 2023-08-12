package me.znepb.zrm.block.signals

import me.znepb.zrm.block.post.AbstractPostMountableBlock
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

abstract class AbstractTrafficSignalBase<T : AbstractPostMountableBlockEntity>
    (settings: Settings, blockEntityFactory: BlockEntityType.BlockEntityFactory<T>)
    : AbstractPostMountableBlock<T>(settings, blockEntityFactory)
{
    fun beforeBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity = world.getBlockEntity(pos)

        if(blockEntity is AbstractTrafficSignalBlockEntityBase) {
            blockEntity.remove()
        }
    }
}