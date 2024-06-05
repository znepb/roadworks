package me.znepb.roadworks.block.signals.impl

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.roadworks.block.signals.SignalType
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class OneHeadTrafficSignalGreenBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractTrafficSignalBlockEntity(
        pos, state,
        Registry.ModBlockEntities.ONE_HEAD_TRAFFIC_SIGNAL_GREEN_BLOCK_ENTITY,
        SignalType.ONE_HEAD_GREEN
    )