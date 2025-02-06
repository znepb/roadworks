package me.znepb.roadworks.container

import me.znepb.roadworks.RoadworksRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class WallContainer(settings: Settings) : AttachmentContainer(settings) {
    companion object {
        fun blockEntity(world: BlockView, pos: BlockPos): WallContainerBlockEntity? =
            world.getBlockEntity(pos, RoadworksRegistry.ModBlockEntities.WALL_CONTAINER_BLOCK_ENTITY).orElse(null)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, RoadworksRegistry.ModBlockEntities.WALL_CONTAINER_BLOCK_ENTITY, WallContainerBlockEntity.Companion::onTick)
    }

    override fun getCullingShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = WallContainerBlockEntity(pos, state)
    override fun getPickStack(world: BlockView?, pos: BlockPos?, state: BlockState?) = ItemStack.EMPTY
}