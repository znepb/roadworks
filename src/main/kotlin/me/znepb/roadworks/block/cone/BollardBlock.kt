package me.znepb.roadworks.block.cone

import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.enums.BlockHalf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class BollardBlock(settings: Settings) : DoubleHighConeBlock(settings) {
    companion object {
        val SHAPE_BOTTOM = createCuboidShape(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)
        val SHAPE_TOP = createCuboidShape(7.0, 0.0, 7.0, 9.0, 2.0, 9.0)
        val SHAPE_TOP_COLLISION = createCuboidShape(7.0, 0.0, 7.0, 9.0, 8.0, 9.0)
    }

    fun getShape(state: BlockState): VoxelShape {
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else SHAPE_TOP
    }

    override fun getCullingShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return getShape(state)
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else SHAPE_TOP_COLLISION
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getShape(state)
    }
}