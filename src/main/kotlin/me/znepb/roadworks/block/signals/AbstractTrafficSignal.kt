package me.znepb.roadworks.block.signals

import me.znepb.roadworks.block.post.AbstractPostMountableBlock
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import net.minecraft.block.entity.BlockEntityType

abstract class AbstractTrafficSignal<T : AbstractPostMountableBlockEntity>
    (settings: Settings, blockEntityFactory: BlockEntityType.BlockEntityFactory<T>)
    : AbstractPostMountableBlock<T>(settings, blockEntityFactory)