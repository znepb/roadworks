package me.znepb.roadworks.util

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext

object BlockItemUtils {
    fun canPlace(context: ItemPlacementContext, state: BlockState): Boolean {
        val playerEntity = context.player
        val shapeContext = if (playerEntity == null) ShapeContext.absent() else ShapeContext.of(playerEntity)
        return (state.canPlaceAt(
            context.world,
            context.blockPos
        )) && context.world.canPlace(state, context.blockPos, shapeContext)
    }
}