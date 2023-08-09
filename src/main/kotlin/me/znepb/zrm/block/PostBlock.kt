package me.znepb.zrm.block

import me.znepb.zrm.Registry
import me.znepb.zrm.block.entity.PostBlockEntity
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

open class PostBlock(
    settings: Settings,
    val size: String) : BlockWithEntity(settings), BlockEntityProvider
{
    companion object {
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

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.POST_BLOCK_ENTITY, PostBlockEntity.Companion::onTick)
    }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
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

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState? {
        (world.getBlockEntity(pos) as PostBlockEntity?)?.getPlacementState(pos)
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        (ctx.world.getBlockEntity(ctx.blockPos) as PostBlockEntity?)?.getPlacementState(ctx.blockPos)
        return super.getPlacementState(ctx)
    }

    private fun pickSideShape(state: Int, thickShape: VoxelShape, mediumShape: VoxelShape, thinShape: VoxelShape): VoxelShape {
        return when(state) {
            3 -> {
                when(size) {
                    "medium" -> mediumShape
                    "thin" -> thinShape
                    else -> thickShape
                }
            }
            2 -> when(size) {
                "thin" -> thinShape
                else -> mediumShape
            }
            1 -> thinShape
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

    private fun getShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as PostBlockEntity?
            ?: return VoxelShapes.empty()

        var shape = this.getMidsectionShape()
        if (blockEntity.footer) shape = VoxelShapes.union(shape, this.getFooterShape())
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.up, TOP_SHAPE_THICK, TOP_SHAPE_MEDIUM, TOP_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.down, BOTTOM_SHAPE_THICK, BOTTOM_SHAPE_MEDIUM, BOTTOM_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.north, NORTH_SHAPE_THICK, NORTH_SHAPE_MEDIUM, NORTH_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.east, EAST_SHAPE_THICK, EAST_SHAPE_MEDIUM, EAST_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.south, SOUTH_SHAPE_THICK, SOUTH_SHAPE_MEDIUM, SOUTH_SHAPE_THIN))
        shape = VoxelShapes.union(shape, pickSideShape(blockEntity.west, WEST_SHAPE_THICK, WEST_SHAPE_MEDIUM, WEST_SHAPE_THIN))

        return shape
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return PostBlockEntity(pos, state)
    }
}
