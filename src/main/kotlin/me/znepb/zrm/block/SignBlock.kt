package me.znepb.zrm.block

import me.znepb.zrm.Main.logger
import me.znepb.zrm.Registry
import me.znepb.zrm.datagen.TagProvider.Companion.POSTS
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Property
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import kotlin.math.log

class SignBlock(settings: Settings): Block(settings) {
    //companion object {
    //    val TYPE = EnumProperty.of("type", SignType::class.java)
    //    val DOWN = EnumProperty.of("down", PostBlock.PostConnection::class.java)
    //    val UP = EnumProperty.of("up", PostBlock.PostConnection::class.java)
    //    val NORTH = EnumProperty.of("north", PostBlock.PostConnection::class.java)
    //    val SOUTH = EnumProperty.of("south", PostBlock.PostConnection::class.java)
    //    val EAST = EnumProperty.of("east", PostBlock.PostConnection::class.java)
    //    val WEST = EnumProperty.of("west", PostBlock.PostConnection::class.java)
    //    val MAX_THICKNESS = EnumProperty.of("max_thickness", PostBlock.PostConnection::class.java)
    //    val FACING = HorizontalFacingBlock.FACING
//
    //    val DIRECTIONS = arrayOf(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST)
    //    val SIDES = arrayOf(UP, DOWN, NORTH, EAST, SOUTH, WEST)
//
    //    val SIGN_SHAPE_WALL_NORTH = createCuboidShape(0.0, 0.0, 15.5, 16.0, 16.0, 16.0)
    //    val SIGN_SHAPE_POST_NONE_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, -7.75)
    //    val SIGN_SHAPE_POST_THIN_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, -8.75)
    //    val SIGN_SHAPE_POST_MEDIUM_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, -9.75)
    //    val SIGN_SHAPE_POST_THICK_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, -10.75)
//
    //    val SIGN_SHAPE_WALL_SOUTH = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 0.5)
    //    val SIGN_SHAPE_POST_NONE_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, 7.75)
    //    val SIGN_SHAPE_POST_THIN_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, 8.75)
    //    val SIGN_SHAPE_POST_MEDIUM_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, 9.75)
    //    val SIGN_SHAPE_POST_THICK_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, 10.75)
    //}
//
    //override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
    //    return true
    //}
//
    //override fun getRenderType(state: BlockState?): BlockRenderType {
    //    return BlockRenderType.MODEL
    //}
//
    //override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
    //    return getPlacementState(ctx.world, ctx.blockPos, ctx, null)
    //}

    // TODO: make a util file to put these shared post functions in

    //private fun getConnectionInt(state: BlockState, thisState: BlockState, dir: Direction): Int {
    //    if(thisState.get(FACING) == dir) return 0
//
    //    if (state.isOf(Registry.ModBlocks.THICK_POST)) {
    //        return 3
    //    } else if(state.isOf(Registry.ModBlocks.POST)) {
    //        return 2
    //    } else if(state.isOf(Registry.ModBlocks.THIN_POST)) {
    //        return 1
    //    }
//
    //    return 0
    //}

    //private fun canConnect(state: BlockState, thisState: BlockState, dir: Direction): Int {
    //    return when(dir) {
    //        Direction.DOWN -> getConnectionInt(state, thisState, dir)
    //        Direction.UP -> getConnectionInt(state, thisState, dir)
    //        Direction.NORTH -> getConnectionInt(state, thisState, dir)
    //        Direction.EAST -> getConnectionInt(state, thisState, dir)
    //        Direction.SOUTH -> getConnectionInt(state, thisState, dir)
    //        Direction.WEST -> getConnectionInt(state, thisState, dir)
    //        else -> 0
    //    }
    //}
//
    //private fun getConnectionName(number: Int): PostBlock.PostConnection {
    //    return when(number) {
    //        1 -> PostBlock.PostConnection.THIN
    //        2 -> PostBlock.PostConnection.MEDIUM
    //        3 -> PostBlock.PostConnection.THICK
    //        else -> PostBlock.PostConnection.NONE
    //    }
    //}
//
    //private fun getPlacementState(world: BlockView, pos: BlockPos, ctx: ItemPlacementContext?, state: BlockState?): BlockState {
    //    var defaultState = this.defaultState
    //    if(state != null) defaultState = state
//
    //    val stateDown = world.getBlockState(pos.down())
    //    val stateUp = world.getBlockState(pos.up())
    //    val stateNorth = world.getBlockState(pos.north())
    //    val stateEast = world.getBlockState(pos.east())
    //    val stateSouth = world.getBlockState(pos.south())
    //    val stateWest = world.getBlockState(pos.west())
//
    //    if(ctx != null) {
    //        val facing =  ctx.horizontalPlayerFacing.opposite
    //        val placedOnPos = pos.offset(facing.opposite)
    //        val placedOnState = world.getBlockState(placedOnPos)
    //        var isWall = true
//
    //        Direction.values().forEach {
    //            if(it != facing.opposite && world.getBlockState(pos.offset(it)).isIn(POSTS)) {
    //                isWall = false
    //            }
    //        }
//
    //        if(placedOnState.isTransparent(world, placedOnPos) && !placedOnState.isIn(POSTS)) {
    //            isWall = false
    //        }
//
    //        defaultState = defaultState.with(FACING, facing)
    //                                   .with(TYPE, if(isWall) SignType.WALL else SignType.POST)
//
    //        logger.info("Placed on wall? $isWall. facing $facing")
    //    }
//
    //    if(defaultState.get(TYPE) == SignType.WALL) {
    //        return defaultState
    //    } else {
    //        val downConn = this.canConnect(stateDown, defaultState, Direction.DOWN)
    //        val upConn = this.canConnect(stateUp, defaultState, Direction.UP)
    //        val northConn = this.canConnect(stateNorth, defaultState, Direction.NORTH)
    //        val eastConn = this.canConnect(stateEast, defaultState, Direction.EAST)
    //        val southConn = this.canConnect(stateSouth, defaultState, Direction.SOUTH)
    //        val westConn = this.canConnect(stateWest, defaultState, Direction.WEST)
    //        val largest = downConn.coerceAtLeast(upConn.coerceAtLeast(northConn.coerceAtLeast(eastConn.coerceAtLeast(southConn.coerceAtLeast((westConn))))))
    //        val largestConn = getConnectionName(largest)
    //        logger.info("Largest size is $largest or $largestConn")
//
    //        return defaultState
    //            .with(DOWN, getConnectionName(downConn))
    //            .with(UP, getConnectionName(upConn))
    //            .with(NORTH, getConnectionName(northConn))
    //            .with(EAST, getConnectionName(eastConn))
    //            .with(SOUTH, getConnectionName(southConn))
    //            .with(WEST, getConnectionName(westConn))
    //            .with(MAX_THICKNESS, largestConn)
    //    }
    //}
//
    //override fun getStateForNeighborUpdate(
    //    state: BlockState,
    //    direction: Direction,
    //    neighborState: BlockState,
    //    world: WorldAccess,
    //    pos: BlockPos,
    //    neighborPos: BlockPos
    //): BlockState {
    //    return this.getPlacementState(world, pos, null, state)
    //}
//
    //enum class SignType(private var s: String) : StringIdentifiable {
    //    WALL("wall"),
    //    POST("post");
//
    //    override fun asString(): String {
    //        return s
    //    }
    //}
//
    //override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
    //    builder?.add(
    //        *arrayOf<Property<*>>(
    //            TYPE, UP, DOWN, NORTH, EAST, SOUTH, WEST, MAX_THICKNESS, FACING
    //        ))
    //}
//
    //override fun getCollisionShape(
    //    state: BlockState,
    //    world: BlockView,
    //    pos: BlockPos,
    //    context: ShapeContext
    //): VoxelShape {
    //    return this.getShape(state)
    //}
//
    //override fun getOutlineShape(
    //    state: BlockState,
    //    world: BlockView,
    //    pos: BlockPos,
    //    context: ShapeContext
    //): VoxelShape {
    //    return this.getShape(state)
    //}
//
    //private fun pickSideShape(state: PostBlock.PostConnection, thickShape: VoxelShape, mediumShape: VoxelShape, thinShape: VoxelShape): VoxelShape {
    //    return when(state) {
    //        PostBlock.PostConnection.THICK -> thickShape
    //        PostBlock.PostConnection.MEDIUM -> mediumShape
    //        PostBlock.PostConnection.THIN -> thinShape
    //        else -> VoxelShapes.empty()
    //    }
    //}
//
    //private fun getShape(state: BlockState): VoxelShape {
    //    var signShape = when(state.get(TYPE)) {
    //        SignType.WALL -> when(state.get(FACING)) {
    //            NORTH -> SIGN_SHAPE_WALL_NORTH
//
    //            SOUTH -> SIGN_SHAPE_WALL_SOUTH
    //            else -> VoxelShapes.empty()
    //        }
    //        SignType.POST -> VoxelShapes.union(when(state.get(MAX_THICKNESS)) {
    //                PostBlock.PostConnection.THIN -> when(state.get(FACING)) {
    //                    NORTH -> SIGN_SHAPE_POST_THIN_NORTH
//
    //                    SOUTH -> SIGN_SHAPE_POST_THIN_SOUTH
    //                    else -> VoxelShapes.empty()
    //                }
    //                PostBlock.PostConnection.MEDIUM -> when(state.get(FACING)) {
    //                    NORTH -> SIGN_SHAPE_POST_MEDIUM_NORTH
//
    //                    SOUTH -> SIGN_SHAPE_POST_MEDIUM_SOUTH
    //                    else -> VoxelShapes.empty()
    //                }
    //                PostBlock.PostConnection.THICK -> when(state.get(FACING)) {
    //                    NORTH -> SIGN_SHAPE_POST_THICK_NORTH
//
    //                    SOUTH -> SIGN_SHAPE_POST_THICK_SOUTH
    //                    else -> VoxelShapes.empty()
    //                }
    //                else -> when(state.get(FACING)) {
    //                    NORTH -> SIGN_SHAPE_POST_NONE_NORTH
//
    //                    SOUTH -> SIGN_SHAPE_POST_NONE_SOUTH
    //                    else -> VoxelShapes.empty()
    //                }
    //            }, when(state.get(MAX_THICKNESS)) {
    //                PostBlock.PostConnection.THIN -> PostBlock.MIDSECTION_SHAPE_THIN
    //                PostBlock.PostConnection.MEDIUM -> PostBlock.MIDSECTION_SHAPE_MEDIUM
    //                PostBlock.PostConnection.THICK -> PostBlock.MIDSECTION_SHAPE_THICK
    //                else -> VoxelShapes.empty()
    //            })
    //        else -> VoxelShapes.empty()
    //    }
//
    //    if(state.get(TYPE) == SignType.POST) {
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(UP),
    //            PostBlock.TOP_SHAPE_THICK,
    //            PostBlock.TOP_SHAPE_MEDIUM,
    //            PostBlock.TOP_SHAPE_THIN
    //        ))
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(DOWN),
    //            PostBlock.BOTTOM_SHAPE_THICK,
    //            PostBlock.BOTTOM_SHAPE_MEDIUM,
    //            PostBlock.BOTTOM_SHAPE_THIN
    //        ))
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(NORTH),
    //            PostBlock.NORTH_SHAPE_THICK,
    //            PostBlock.NORTH_SHAPE_MEDIUM,
    //            PostBlock.NORTH_SHAPE_THIN
    //        ))
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(EAST),
    //            PostBlock.EAST_SHAPE_THICK,
    //            PostBlock.EAST_SHAPE_MEDIUM,
    //            PostBlock.EAST_SHAPE_THIN
    //        ))
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(SOUTH),
    //            PostBlock.SOUTH_SHAPE_THICK,
    //            PostBlock.SOUTH_SHAPE_MEDIUM,
    //            PostBlock.SOUTH_SHAPE_THIN
    //        ))
    //        signShape = VoxelShapes.union(signShape, pickSideShape(state.get(WEST),
    //            PostBlock.WEST_SHAPE_THICK,
    //            PostBlock.WEST_SHAPE_MEDIUM,
    //            PostBlock.WEST_SHAPE_THIN
    //        ))
    //    }
//
    //    return signShape
    //}
}