package me.znepb.zrm.block

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.enums.BlockHalf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class DrumBlock(settings: AbstractBlock.Settings) : DoubleHighConeBlock(settings) {
    companion object {
        val SHAPE_BOTTOM = VoxelShapes.union(
            createCuboidShape(2.5, 0.0, 2.5, 13.5, 1.0, 13.5),
            createCuboidShape(3.5, 1.0, 3.5, 12.5, 1.75, 12.5),
            createCuboidShape(4.0, 1.75, 4.0, 12.0, 16.0, 12.0)
        )

        val SHAPE_TOP_COLLISION = VoxelShapes.union(
            createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0)
        )
        val SHAPE_TOP_NS = createCuboidShape(5.0, 0.0, 7.5, 11.0, 2.0, 8.5)
        val SHAPE_TOP_EW = createCuboidShape(7.5, 0.0, 5.0, 8.5, 2.0, 11.0)
    }

    fun getShape(state: BlockState): VoxelShape {
        val facing = state.get(FACING)
        return if(state.get(HALF) == BlockHalf.BOTTOM) SHAPE_BOTTOM else
            if(facing == Direction.NORTH || facing == Direction.SOUTH) SHAPE_TOP_NS else SHAPE_TOP_EW
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