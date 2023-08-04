package me.znepb.zrm.block;

import me.znepb.zrm.Registry
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.DirectionProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class ThickPostBlock(settings: Settings) : Block(settings) {
    init {
        this.defaultState = this.defaultState.with(FOOTER, false)
            .with(TOP, false)
            .with(BOTTOM, false)
            .with(NORTH, PostConnection.NONE)
            .with(SOUTH, PostConnection.NONE)
            .with(EAST, PostConnection.NONE)
            .with(WEST, PostConnection.NONE)
    }

    companion object {
        val FOOTER = BooleanProperty.of("footer")
        val BOTTOM = BooleanProperty.of("bottom")
        val TOP = BooleanProperty.of("top")
        val NORTH = EnumProperty.of("north", PostConnection::class.java)
        val SOUTH = EnumProperty.of("south", PostConnection::class.java)
        val EAST = EnumProperty.of("east", PostConnection::class.java)
        val WEST = EnumProperty.of("west", PostConnection::class.java)

        val BOTTOM_SHAPE = createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0)
        val MIDSECTION_SHAPE = createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
        val TOP_SHAPE = createCuboidShape(5.0, 11.0, 5.0, 11.0, 16.0, 11.0)
        val FOOTER_SHAPE = VoxelShapes.union(BOTTOM_SHAPE, createCuboidShape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0))
        val NORTH_SHAPE_THICK = createCuboidShape(5.0, 5.0, 0.0, 11.0, 11.0, 5.0)
        val EAST_SHAPE_THICK = createCuboidShape(11.0, 5.0, 5.0, 16.0, 11.0, 11.0)
        val SOUTH_SHAPE_THICK = createCuboidShape(5.0, 5.0, 11.0, 11.0, 11.0, 16.0)
        val WEST_SHAPE_THICK = createCuboidShape(0.0, 5.0, 5.0, 5.0, 11.0, 11.0)

    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(FOOTER)
        builder?.add(TOP)
        builder?.add(BOTTOM)
        builder?.add(NORTH)
        builder?.add(SOUTH)
        builder?.add(EAST)
        builder?.add(WEST) }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return this.getShape(state);
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return this.getShape(state);
    }

    private fun canConnect(state: BlockState?, dir: Direction?): Int {
        if(state == null || dir == null) return 0

        return when(dir) {
            Direction.DOWN -> {
                if(state.isOf(Registry.ModBlocks.THICK_POST)) {
                    return 1 // on a post
                } else if(state.isOf(Blocks.AIR)) {
                    return 0 // floating
                } else {
                    return 2 // on ground
                }
            }
            Direction.UP -> if(state.isOf(Registry.ModBlocks.THICK_POST)) 1 else 0
            Direction.NORTH -> if(state.isOf(Registry.ModBlocks.THICK_POST)) 3 else 0
            Direction.EAST -> if(state.isOf(Registry.ModBlocks.THICK_POST)) 3 else 0
            Direction.SOUTH -> if(state.isOf(Registry.ModBlocks.THICK_POST)) 3 else 0
            Direction.WEST -> if(state.isOf(Registry.ModBlocks.THICK_POST)) 3 else 0
            else -> 0
        }
    }

    private fun getConnectionName(number: Int): PostConnection {
        return when(number) {
            1 -> PostConnection.THINNER
            2 -> PostConnection.THIN
            3 -> PostConnection.THICK
            else -> PostConnection.NONE
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return this.getPlacementState(ctx?.world, ctx?.blockPos, null)
    }

    fun getPlacementState(world: BlockView?, pos: BlockPos?, state: BlockState?): BlockState? {
        var defaultState = this.defaultState
        if(state != null) defaultState = state

        val stateDown = world?.getBlockState(pos?.down())
        val stateUp = world?.getBlockState(pos?.up())
        val stateNorth = world?.getBlockState(pos?.north())
        val stateEast = world?.getBlockState(pos?.east())
        val stateSouth = world?.getBlockState(pos?.south())
        val stateWest = world?.getBlockState(pos?.west())

        val downConn = this.canConnect(stateDown, Direction.DOWN)

        return defaultState
            ?.with(FOOTER, downConn == 2)
            ?.with(BOTTOM, downConn == 1)
            ?.with(TOP, this.canConnect(stateUp, Direction.UP) == 1)
            ?.with(NORTH, getConnectionName(this.canConnect(stateNorth, Direction.NORTH)))
            ?.with(EAST, getConnectionName(this.canConnect(stateEast, Direction.EAST)))
            ?.with(SOUTH, getConnectionName(this.canConnect(stateSouth, Direction.SOUTH)))
            ?.with(WEST, getConnectionName(this.canConnect(stateWest, Direction.WEST)))
    }

    override fun getStateForNeighborUpdate(
        state: BlockState?,
        direction: Direction?,
        neighborState: BlockState?,
        world: WorldAccess?,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState? {
        return this.getPlacementState(world, pos, state)
    }

    private fun getShape(state: BlockState): VoxelShape {
        val footer = state.get(FOOTER)
        val bottom = state.get(BOTTOM)
        val top = state.get(TOP)
        val north = state.get(NORTH)
        val east = state.get(EAST)
        val south = state.get(SOUTH)
        val west = state.get(WEST)
        var shape = MIDSECTION_SHAPE

        if(footer) shape = VoxelShapes.union(shape, FOOTER_SHAPE)
        if(bottom) shape = VoxelShapes.union(shape, BOTTOM_SHAPE)
        if(top) shape = VoxelShapes.union(shape, TOP_SHAPE)

        if(north == PostConnection.THICK) shape = VoxelShapes.union(shape, NORTH_SHAPE_THICK)
        if(east == PostConnection.THICK) shape = VoxelShapes.union(shape, EAST_SHAPE_THICK)
        if(south == PostConnection.THICK) shape = VoxelShapes.union(shape, SOUTH_SHAPE_THICK)
        if(west == PostConnection.THICK) shape = VoxelShapes.union(shape, WEST_SHAPE_THICK)


        return shape
    }
}
