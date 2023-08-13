package me.znepb.zrm.block.signals.impl

import me.znepb.zrm.Registry
import me.znepb.zrm.block.signals.AbstractFiveHeadSignal
import me.znepb.zrm.block.signals.AbstractThreeHeadSignal
import me.znepb.zrm.block.signals.AbstractTrafficSignalBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.world.World

class FiveHeadTrafficSignalLeft(settings: Settings)
    : AbstractFiveHeadSignal<FiveHeadTrafficSignalLeftBlockEntity>(settings, ::FiveHeadTrafficSignalLeftBlockEntity)
{
    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(
            type,
            Registry.ModBlockEntities.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY,
            AbstractTrafficSignalBlockEntity.Companion::onTick
        )
    }
}