package me.znepb.zrm.block

import me.znepb.zrm.Registry
import me.znepb.zrm.block.entity.SignBlockEntity
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class SignBlock(settings: Settings, val frontTexture: String, val backTexture: String): BlockWithEntity(settings), BlockEntityProvider {
    var placementContext: ItemPlacementContext? = null
    companion object {
        val SIGN_SHAPE_WALL_NORTH = createCuboidShape(0.0, 0.0, 15.5, 16.0, 16.0, 16.0)
        val SIGN_SHAPE_POST_NONE_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, (-7.75 / 16))
        val SIGN_SHAPE_POST_THIN_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, (-8.75 / 16))
        val SIGN_SHAPE_POST_MEDIUM_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, (-9.75 / 16))
        val SIGN_SHAPE_POST_THICK_NORTH = SIGN_SHAPE_WALL_NORTH.offset(0.0, 0.0, (-10.75 / 16))

        val SIGN_SHAPE_WALL_WEST = createCuboidShape(15.5, 0.0, 0.0, 16.0, 16.0, 16.0)
        val SIGN_SHAPE_POST_NONE_WEST = SIGN_SHAPE_WALL_WEST.offset((-7.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_THIN_WEST = SIGN_SHAPE_WALL_WEST.offset((-8.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_MEDIUM_WEST = SIGN_SHAPE_WALL_WEST.offset((-9.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_THICK_WEST = SIGN_SHAPE_WALL_WEST.offset((-10.75 / 16), 0.0, 0.0)

        val SIGN_SHAPE_WALL_SOUTH = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 0.5)
        val SIGN_SHAPE_POST_NONE_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, (7.75 / 16))
        val SIGN_SHAPE_POST_THIN_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, (8.75 / 16))
        val SIGN_SHAPE_POST_MEDIUM_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, (9.75 / 16))
        val SIGN_SHAPE_POST_THICK_SOUTH = SIGN_SHAPE_WALL_SOUTH.offset(0.0, 0.0, (10.75 / 16))

        val SIGN_SHAPE_WALL_EAST = createCuboidShape(0.0, 0.0, 0.0, 0.5, 16.0, 16.0)
        val SIGN_SHAPE_POST_NONE_EAST = SIGN_SHAPE_WALL_EAST.offset((7.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_THIN_EAST = SIGN_SHAPE_WALL_EAST.offset((8.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_MEDIUM_EAST = SIGN_SHAPE_WALL_EAST.offset((9.75 / 16), 0.0, 0.0)
        val SIGN_SHAPE_POST_THICK_EAST = SIGN_SHAPE_WALL_EAST.offset((10.75 / 16), 0.0, 0.0)

    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, SignBlockEntity.Companion::onTick)
    }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState? {
        (world.getBlockEntity(pos) as SignBlockEntity?)?.getPlacementState(placementContext)
        placementContext = null
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        placementContext = ctx
        (ctx.world.getBlockEntity(ctx.blockPos) as SignBlockEntity?)?.getPlacementState(ctx)
        return super.getPlacementState(ctx)
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return this.getShape(world, pos)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return this.getShape(world, pos)
    }

    private fun pickSideShape(size: Int, thickShape: VoxelShape, mediumShape: VoxelShape, thinShape: VoxelShape): VoxelShape {
        return when(size) {
            3 -> thickShape
            2 -> mediumShape
            1 -> thinShape
            else -> VoxelShapes.empty()
        }
    }

    private fun getShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as SignBlockEntity?
            ?: return VoxelShapes.empty()
        val maxThickness = SignBlockEntity.getThickest(blockEntity)

        var signShape = if(blockEntity.wall) {
            when (blockEntity.signFacing) {
                Direction.NORTH.id -> SIGN_SHAPE_WALL_NORTH
                Direction.EAST.id -> SIGN_SHAPE_WALL_EAST
                Direction.SOUTH.id -> SIGN_SHAPE_WALL_SOUTH
                Direction.WEST.id -> SIGN_SHAPE_WALL_WEST
                else -> VoxelShapes.empty()
            }
        } else {
            when(maxThickness) {
                1 -> when (blockEntity.signFacing) {
                    Direction.NORTH.id -> SIGN_SHAPE_POST_THIN_NORTH
                    Direction.EAST.id -> SIGN_SHAPE_POST_THIN_EAST
                    Direction.SOUTH.id -> SIGN_SHAPE_POST_THIN_SOUTH
                    Direction.WEST.id -> SIGN_SHAPE_POST_THIN_WEST
                    else -> VoxelShapes.empty()
                }

                2 -> when (blockEntity.signFacing) {
                    Direction.NORTH.id -> SIGN_SHAPE_POST_MEDIUM_NORTH
                    Direction.EAST.id -> SIGN_SHAPE_POST_MEDIUM_EAST
                    Direction.SOUTH.id -> SIGN_SHAPE_POST_MEDIUM_SOUTH
                    Direction.WEST.id -> SIGN_SHAPE_POST_MEDIUM_WEST
                    else -> VoxelShapes.empty()
                }

                3 -> when (blockEntity.signFacing) {
                    Direction.NORTH.id -> SIGN_SHAPE_POST_THICK_NORTH
                    Direction.EAST.id -> SIGN_SHAPE_POST_THICK_EAST
                    Direction.SOUTH.id -> SIGN_SHAPE_POST_THICK_SOUTH
                    Direction.WEST.id -> SIGN_SHAPE_POST_THICK_WEST
                    else -> VoxelShapes.empty()
                }

                else -> when (blockEntity.signFacing) {
                    Direction.NORTH.id -> SIGN_SHAPE_POST_NONE_NORTH
                    Direction.EAST.id -> SIGN_SHAPE_POST_NONE_EAST
                    Direction.SOUTH.id -> SIGN_SHAPE_POST_NONE_SOUTH
                    Direction.WEST.id -> SIGN_SHAPE_POST_NONE_WEST
                    else -> VoxelShapes.empty()
                }
            }
        }

        if(!blockEntity.wall) {
            signShape = VoxelShapes.union(
                signShape,
                when(maxThickness) {
                    1 -> PostBlock.MIDSECTION_SHAPE_THIN
                    2 -> PostBlock.MIDSECTION_SHAPE_MEDIUM
                    3 -> PostBlock.MIDSECTION_SHAPE_THICK
                    else -> VoxelShapes.empty()
                }
            )

            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.up,
                PostBlock.TOP_SHAPE_THICK,
                PostBlock.TOP_SHAPE_MEDIUM,
                PostBlock.TOP_SHAPE_THIN
            ))
            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.down,
                PostBlock.BOTTOM_SHAPE_THICK,
                PostBlock.BOTTOM_SHAPE_MEDIUM,
                PostBlock.BOTTOM_SHAPE_THIN
            ))
            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.north,
                PostBlock.NORTH_SHAPE_THICK,
                PostBlock.NORTH_SHAPE_MEDIUM,
                PostBlock.NORTH_SHAPE_THIN
            ))
            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.east,
                PostBlock.EAST_SHAPE_THICK,
                PostBlock.EAST_SHAPE_MEDIUM,
                PostBlock.EAST_SHAPE_THIN
            ))
            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.south,
                PostBlock.SOUTH_SHAPE_THICK,
                PostBlock.SOUTH_SHAPE_MEDIUM,
                PostBlock.SOUTH_SHAPE_THIN
            ))
            signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.west,
                PostBlock.WEST_SHAPE_THICK,
                PostBlock.WEST_SHAPE_MEDIUM,
                PostBlock.WEST_SHAPE_THIN
            ))
        }

        return signShape
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val be = SignBlockEntity(pos, state)
        this.placementContext?.let { be.setContext(it) }
        return be
    }
}