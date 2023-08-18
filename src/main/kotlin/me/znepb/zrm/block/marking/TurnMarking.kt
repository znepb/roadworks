package me.znepb.zrm.block.marking

import me.znepb.zrm.Registry
import me.znepb.zrm.block.marking.OneSideFilledMarking.Companion.getCardinalDirectionFilled
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class TurnMarking : HorizontalFacingBlock(Settings.copy(Blocks.WHITE_CONCRETE)) {
    companion object {
        val MARKING_SHAPE = createCuboidShape(0.0, 0.05, 0.0, 16.0, 0.1, 16.0)

        val INSIDE_FILL = BooleanProperty.of("inside_fill")
        val OUTSIDE_FILL = BooleanProperty.of("outside_fill")
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(INSIDE_FILL, false)
            .with(OUTSIDE_FILL, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(INSIDE_FILL)
        builder.add(OUTSIDE_FILL)
    }

    private fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        val facing = state.get(Properties.HORIZONTAL_FACING)

        // Inside
        val right = world.getBlockState(pos.offset(facing.rotateYClockwise()))
        val back = world.getBlockState(pos.offset(facing.opposite))

        val inside = if(right.block is OneSideFilledMarking && back.block is OneSideFilledMarking) {
            val rightFilled = getCardinalDirectionFilled(right, facing.opposite)
            val backFilled = getCardinalDirectionFilled(back, facing.rotateYClockwise())

            rightFilled && backFilled
        } else false

        // outside

        val left = world.getBlockState(pos.offset(facing.rotateYCounterclockwise()))
        val front = world.getBlockState(pos.offset(facing))

        val outside = if(left.block is OneSideFilledMarking && front.block is OneSideFilledMarking) {
            val leftFilled = getCardinalDirectionFilled(left, facing.rotateYClockwise())
            val frontFilled = getCardinalDirectionFilled(front, facing.opposite)

            leftFilled && frontFilled
        } else left.isOf(Registry.ModBlocks.WHITE_INFILL_MARKING) && front.isOf(Registry.ModBlocks.WHITE_INFILL_MARKING)

        return state.with(INSIDE_FILL, inside).with(OUTSIDE_FILL, outside)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return getState(state, world, pos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val placement = super.getPlacementState(ctx)!!.with(Properties.HORIZONTAL_FACING, ctx.horizontalPlayerFacing)
        return getState(placement, ctx.world, ctx.blockPos)
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