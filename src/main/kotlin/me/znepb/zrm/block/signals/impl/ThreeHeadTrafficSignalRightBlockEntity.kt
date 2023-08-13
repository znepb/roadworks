package me.znepb.zrm.block.signals.impl

import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import me.znepb.zrm.block.signals.SignalType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class ThreeHeadTrafficSignalRightBlockEntity(pos: BlockPos, state: BlockState)
    : AbstractTrafficSignalBlockEntity(
        pos, state,
        Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY,
        SignalType.THREE_HEAD_RIGHT
    )