package me.znepb.roadworks.block.signals

import me.znepb.roadworks.Registry
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

open class PedestrianSignalBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractTrafficSignalBlockEntity(
    pos, state,
    Registry.ModBlockEntities.PEDESTRIAN_SIGNAL_BLOCK_ENTITY,
    SignalType.PEDESTRIAN
)