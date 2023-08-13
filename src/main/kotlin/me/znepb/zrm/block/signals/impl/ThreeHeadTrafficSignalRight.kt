package me.znepb.zrm.block.signals.impl

import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.AbstractThreeHeadSignal
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.world.World

class ThreeHeadTrafficSignalRight(settings: Settings)
    : AbstractThreeHeadSignal<ThreeHeadTrafficSignalRightBlockEntity>
    (settings, ::ThreeHeadTrafficSignalRightBlockEntity) {
    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(
            type,
            Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY,
            AbstractTrafficSignalBlockEntity.Companion::onTick
        )
    }
}