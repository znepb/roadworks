package me.znepb.zrm.block.signals.impl

import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.zrm.block.signals.SignalType
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class ThreeHeadTrafficSignalBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractTrafficSignalBlockEntity(
        pos, state,
        Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY,
        SignalType.THREE_HEAD
    )