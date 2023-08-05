package me.znepb.zrm.block

import me.znepb.zrm.Registry
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.registry.RegistryOps.RegistryInfoGetter
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

open class PostBlock(
    settings: Settings,
    private val size: String) : Block(settings)
{
    init {
        this.defaultState = this.defaultState.with(FOOTER, false)
            .with(UP, PostConnection.NONE)
            .with(DOWN, PostConnection.NONE)
            .with(NORTH, PostConnection.NONE)
            .with(SOUTH, PostConnection.NONE)
            .with(EAST, PostConnection.NONE)
            .with(WEST, PostConnection.NONE)
    }

    companion object {
        val FOOTER = BooleanProperty.of("footer")
        val DOWN = EnumProperty.of("down", PostConnection::class.java)
        val UP = EnumProperty.of("up", PostConnection::class.java)
        val NORTH = EnumProperty.of("north", PostConnection::class.java)
        val SOUTH = EnumProperty.of("south", PostConnection::class.java)
        val EAST = EnumProperty.of("east", PostConnection::class.java)
        val WEST = EnumProperty.of("west", PostConnection::class.java)

        val BOTTOM_SHAPE_THICK = createCuboidShape(5.0, 0.0, 5.0, 11.0, 5.0, 11.0)
        val MIDSECTION_SHAPE_THICK = createCuboidShape(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
        val TOP_SHAPE_THICK = createCuboidShape(5.0, 11.0, 5.0, 11.0, 16.0, 11.0)
        val FOOTER_SHAPE_THICK = VoxelShapes.union(BOTTOM_SHAPE_THICK, createCuboidShape(3.0, 0.0, 3.0, 13.0, 3.0, 13.0))
        val NORTH_SHAPE_THICK = createCuboidShape(5.0, 5.0, 0.0, 11.0, 11.0, 5.0)
        val EAST_SHAPE_THICK = createCuboidShape(11.0, 5.0, 5.0, 16.0, 11.0, 11.0)
        val SOUTH_SHAPE_THICK = createCuboidShape(5.0, 5.0, 11.0, 11.0, 11.0, 16.0)
        val WEST_SHAPE_THICK = createCuboidShape(0.0, 5.0, 5.0, 5.0, 11.0, 11.0)

        val BOTTOM_SHAPE_MEDIUM = createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0)
        val MIDSECTION_SHAPE_MEDIUM = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0)
        val TOP_SHAPE_MEDIUM = createCuboidShape(6.0, 10.0, 6.0, 10.0, 16.0, 10.0)
        val FOOTER_SHAPE_MEDIUM = VoxelShapes.union(BOTTOM_SHAPE_MEDIUM, createCuboidShape(5.0, 0.0, 5.0, 11.0, 2.0, 11.0))
        val NORTH_SHAPE_MEDIUM = createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 6.0)
        val EAST_SHAPE_MEDIUM = createCuboidShape(10.0, 6.0, 6.0, 16.0, 10.0, 10.0)
        val SOUTH_SHAPE_MEDIUM = createCuboidShape(6.0, 6.0, 10.0, 10.0, 10.0, 16.0)
        val WEST_SHAPE_MEDIUM = createCuboidShape(0.0, 6.0, 6.0, 6.0, 10.0, 10.0)

        val BOTTOM_SHAPE_THIN = createCuboidShape(7.0, 0.0, 7.0, 9.0, 7.0, 9.0)
        val MIDSECTION_SHAPE_THIN = createCuboidShape(7.0, 7.0, 7.0, 9.0, 9.0, 9.0)
        val TOP_SHAPE_THIN = createCuboidShape(7.0, 9.0, 7.0, 9.0, 16.0, 9.0)
        val FOOTER_SHAPE_THIN = VoxelShapes.union(BOTTOM_SHAPE_THIN, createCuboidShape(6.0, 0.0, 6.0, 10.0, 1.0, 10.0))
        val NORTH_SHAPE_THIN = createCuboidShape(7.0, 7.0, 0.0, 9.0, 9.0, 7.0)
        val EAST_SHAPE_THIN = createCuboidShape(9.0, 7.0, 7.0, 16.0, 9.0, 9.0)
        val SOUTH_SHAPE_THIN = createCuboidShape(7.0, 7.0, 9.0, 9.0, 9.0, 16.0)
        val WEST_SHAPE_THIN = createCuboidShape(0.0, 7.0, 7.0, 7.0, 9.0, 9.0)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(
            *arrayOf<Property<*>>(
                FOOTER, UP, DOWN, NORTH, EAST, SOUTH, WEST
            ))
    }

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

    private fun getConnectionInt(state: BlockState): Int {
        if (state.isOf(Registry.ModBlocks.THICK_POST)) {
            return 3
        } else if(state.isOf(Registry.ModBlocks.POST)) {
            return 2
        } else if(state.isOf(Registry.ModBlocks.THIN_POST)) {
            return 1
        }

        return 0
    }

    private fun canConnect(state: BlockState?, dir: Direction?): Int {
        if(state == null || dir == null) return 0

        return when(dir) {
            Direction.DOWN -> {
                val connectionInt = getConnectionInt(state)

                if(connectionInt > 0) {
                    return connectionInt
                } else if(state.isOf(Blocks.AIR)) {
                    return 0 // floating
                } else {
                    return 4 // on ground
                }
            }
            Direction.UP -> getConnectionInt(state)
            Direction.NORTH -> getConnectionInt(state)
            Direction.EAST -> getConnectionInt(state)
            Direction.SOUTH -> getConnectionInt(state)
            Direction.WEST -> getConnectionInt(state)
            else -> 0
        }
    }

    private fun getConnectionName(number: Int): PostConnection {
        return when(number) {
            1 -> PostConnection.THIN
            2 -> PostConnection.MEDIUM
            3 -> PostConnection.THICK
            else -> PostConnection.NONE
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? {
        return this.getPlacementState(ctx?.world, ctx?.blockPos, null)
    }

    private fun getPlacementState(world: BlockView?, pos: BlockPos?, state: BlockState?): BlockState? {
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
            ?.with(FOOTER, downConn == 4)
            ?.with(DOWN, getConnectionName(this.canConnect(stateDown, Direction.DOWN)))
            ?.with(UP, getConnectionName(this.canConnect(stateUp, Direction.UP)))
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

    private fun pickSideShape(state: PostConnection, thickShape: VoxelShape, mediumShape: VoxelShape, thinShape: VoxelShape): VoxelShape {
        return when(state) {
            PostConnection.THICK -> {
                when(size) {
                    "medium" -> mediumShape
                    "thin" -> thinShape
                    else -> thickShape
                }
            }
            PostConnection.MEDIUM -> when(size) {
                "thin" -> thinShape
                else -> mediumShape
            }
            PostConnection.THIN -> thinShape
            else -> VoxelShapes.empty()
        }
    }

    private fun getMidsectionShape(): VoxelShape {
        return when(size) {
            "medium" -> MIDSECTION_SHAPE_MEDIUM
            "thin" -> MIDSECTION_SHAPE_THIN
            else -> MIDSECTION_SHAPE_THICK
        }
    }

    private fun getFooterShape(): VoxelShape {
        return when(size) {
            "medium" -> FOOTER_SHAPE_MEDIUM
            "thin" -> FOOTER_SHAPE_THIN
            else -> FOOTER_SHAPE_THICK
        }
    }

    private fun getShape(state: BlockState): VoxelShape {
        val footer = state.get(FOOTER)
        var shape = this.getMidsectionShape()

        if(footer) shape = VoxelShapes.union(shape, this.getFooterShape())
        shape = VoxelShapes.union(shape, pickSideShape(state.get(UP), TOP_SHAPE_THICK, TOP_SHAPE_MEDIUM, TOP_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(state.get(DOWN), BOTTOM_SHAPE_THICK, BOTTOM_SHAPE_MEDIUM, BOTTOM_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(state.get(NORTH), NORTH_SHAPE_THICK, NORTH_SHAPE_MEDIUM, NORTH_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(state.get(EAST), EAST_SHAPE_THICK, EAST_SHAPE_MEDIUM, EAST_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(state.get(SOUTH), SOUTH_SHAPE_THICK, SOUTH_SHAPE_MEDIUM, SOUTH_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(state.get(WEST), WEST_SHAPE_THICK, WEST_SHAPE_MEDIUM, WEST_SHAPE_THIN))

        return shape
    }
}
