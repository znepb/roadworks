package me.znepb.zrm.block.signals

import me.znepb.zrm.Registry
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class ThreeHeadTrafficSignalBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractTrafficSignalBlockEntityBase(
        pos, state,
        Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY,
        SignalType.THREE_HEAD
    )