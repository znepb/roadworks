package me.znepb.zrm.block.marking

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks.WHITE_CONCRETE
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class BasicMarking() : HorizontalFacingBlock(Settings.copy(WHITE_CONCRETE)) {
    companion object {
        val MARKING_SHAPE = createCuboidShape(0.0, 0.05, 0.0, 16.0, 0.1, 16.0)
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)!!.with(Properties.HORIZONTAL_FACING, ctx.horizontalPlayerFacing.opposite)
    }

    override fun getCullingShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return MARKING_SHAPE
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return MARKING_SHAPE
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return MARKING_SHAPE
    }
}