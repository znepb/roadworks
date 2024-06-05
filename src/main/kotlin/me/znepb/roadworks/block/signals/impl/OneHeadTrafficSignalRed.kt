package me.znepb.roadworks.block.signals.impl

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.signals.AbstractOneHeadSignal
import me.znepb.roadworks.block.signals.AbstractTrafficSignalBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.world.World

class OneHeadTrafficSignalRed(settings: Settings)
    : AbstractOneHeadSignal<OneHeadTrafficSignalRedBlockEntity>
    (settings, ::OneHeadTrafficSignalRedBlockEntity) {
    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(
            type,
            Registry.ModBlockEntities.ONE_HEAD_TRAFFIC_SIGNAL_RED_BLOCK_ENTITY,
            AbstractTrafficSignalBlockEntity.Companion::onTick
        )
    }
}