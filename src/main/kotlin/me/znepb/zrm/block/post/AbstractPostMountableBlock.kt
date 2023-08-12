package me.znepb.zrm.block.post

import me.znepb.zrm.util.PostThickness
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

abstract class AbstractPostMountableBlock<T : AbstractPostMountableBlockEntity>
    (settings: Settings, private val blockEntityFactory: BlockEntityFactory<T>)
    : BlockWithEntity(settings), BlockEntityProvider
{
    private var placementContext: ItemPlacementContext? = null

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
        (world.getBlockEntity(pos) as AbstractPostMountableBlockEntity?)?.getPlacementState(placementContext)
        placementContext = null
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        placementContext = ctx
        (ctx.world.getBlockEntity(ctx.blockPos) as AbstractPostMountableBlockEntity?)?.getPlacementState(ctx)
        return super.getPlacementState(ctx)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val be = blockEntityFactory.create(pos, state)
        this.placementContext?.let { be.setContext(it) }
        return be
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

    fun pickSideShape(connectingSize: PostThickness, direction: Direction): VoxelShape {
        return when(connectingSize) {
            PostThickness.THICK -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.THICK)
            PostThickness.MEDIUM -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.MEDIUM)
            PostThickness.THIN -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.THIN)
            else -> VoxelShapes.empty()
        }
    }

    fun getShape(world: BlockView, pos: BlockPos): VoxelShape {
        var shape = this.getAttachmentShape(world, pos)
        val blockEntity = world.getBlockEntity(pos)

        if(blockEntity !is AbstractPostMountableBlockEntity) return VoxelShapes.empty()

        shape = VoxelShapes.union(
            shape,
            when(AbstractPostMountableBlockEntity.getThickest(blockEntity)) {
                PostThickness.THIN -> PostBlock.MIDSECTION_SHAPE_THIN
                PostThickness.MEDIUM -> PostBlock.MIDSECTION_SHAPE_MEDIUM
                PostThickness.THICK -> PostBlock.MIDSECTION_SHAPE_THICK
                else -> VoxelShapes.empty()
            }
        )

        Direction.entries.forEach {
            shape = VoxelShapes.union(shape, this.pickSideShape(blockEntity.getDirectionThickness(it), it))
        }

        return shape
    }

    abstract fun getAttachmentShape(world: BlockView, pos: BlockPos): VoxelShape
}