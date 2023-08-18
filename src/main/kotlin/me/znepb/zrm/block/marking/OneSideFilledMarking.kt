package me.znepb.zrm.block.marking

import me.znepb.zrm.Registry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks.WHITE_CONCRETE
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class OneSideFilledMarking() : HorizontalFacingBlock(Settings.copy(WHITE_CONCRETE)) {
    companion object {
        val MARKING_SHAPE = createCuboidShape(0.0, 0.05, 0.0, 16.0, 0.1, 16.0)

        val RIGHT_FILL = BooleanProperty.of("right_fill")
        val LEFT_FILL = BooleanProperty.of("left_fill")

        fun doesBorder(from: BlockState, other: BlockState, side: Side): Boolean {
            if(other.isOf(Registry.ModBlocks.WHITE_INFILL_MARKING)) return true
            if(!from.contains(Properties.HORIZONTAL_FACING)
                || !other.contains(Properties.HORIZONTAL_FACING)) return false

            val thisState = from.get(Properties.HORIZONTAL_FACING)
            val otherState = other.get(Properties.HORIZONTAL_FACING)

            return thisState == otherState || thisState == otherState.opposite
        }

        fun getAbsoluteFromRelative(state: BlockState, relative: Side): Direction {
            return when(state.get(Properties.HORIZONTAL_FACING)) {
                Direction.NORTH -> when(relative) {
                    Side.RIGHT -> Direction.EAST
                    Side.LEFT -> Direction.WEST
                }
                Direction.EAST -> when(relative) {
                    Side.RIGHT -> Direction.SOUTH
                    Side.LEFT -> Direction.NORTH
                }
                Direction.SOUTH -> when(relative) {
                    Side.RIGHT -> Direction.WEST
                    Side.LEFT -> Direction.EAST
                }
                Direction.WEST -> when(relative) {
                    Side.RIGHT -> Direction.NORTH
                    Side.LEFT -> Direction.SOUTH
                }
                else -> Direction.NORTH
            }
        }

        fun getCardinalDirectionFilled(state: BlockState, direction: Direction): Boolean {
            if(state.get(Properties.HORIZONTAL_FACING) == direction &&
                state.get(Properties.HORIZONTAL_FACING) == direction.opposite) return false

            return if(direction == state.get(Properties.HORIZONTAL_FACING).rotateYClockwise())
                state.get(RIGHT_FILL)
            else state.get(LEFT_FILL)
        }
    }

    enum class Side {
        RIGHT, LEFT;

        fun opposite(): Side {
            return if(this == RIGHT) LEFT else RIGHT
        }
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(RIGHT_FILL, false)
            .with(LEFT_FILL, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(RIGHT_FILL)
        builder.add(LEFT_FILL)
    }

    private fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        val directionRight = getAbsoluteFromRelative(state, Side.RIGHT)
        val directionLeft = directionRight.opposite

        val blockLeft = world.getBlockState(pos.offset(directionLeft))
        val blockRight = world.getBlockState(pos.offset(directionRight))

        val connectLeft = doesBorder(state, blockLeft, Side.LEFT)
        val connectRight = doesBorder(state, blockRight, Side.RIGHT)

        return state.with(RIGHT_FILL, connectRight).with(LEFT_FILL, connectLeft)
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
        val placement = super.getPlacementState(ctx)!!.with(Properties.HORIZONTAL_FACING, ctx.horizontalPlayerFacing.opposite)
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