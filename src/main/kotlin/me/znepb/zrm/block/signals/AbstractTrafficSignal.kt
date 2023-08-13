package me.znepb.zrm.block.signals

import me.znepb.zrm.block.post.AbstractPostMountableBlock
import me.znepb.zrm.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

abstract class AbstractTrafficSignal<T : AbstractPostMountableBlockEntity>
    (settings: Settings, blockEntityFactory: BlockEntityType.BlockEntityFactory<T>)
    : AbstractPostMountableBlock<T>(settings, blockEntityFactory)