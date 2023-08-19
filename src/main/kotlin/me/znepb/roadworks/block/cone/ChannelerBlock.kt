package me.znepb.roadworks.block.cone

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.enums.BlockHalf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class ChannelerBlock(settings: Settings) : DoubleHighConeBlock(settings) {
    companion object {
        val SHAPE_BOTTOM = VoxelShapes.union(
            createCuboidShape(6.0, 1.0, 6.0, 10.0, 16.0, 10.0),
            createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0)
        )
        val SHAPE_TOP = createCuboidShape(6.0, 0.0, 6.0, 10.0, 12.4, 10.0)
    }

    override fun getCullingShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else SHAPE_TOP
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else SHAPE_TOP
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else SHAPE_TOP
    }
}