package me.znepb.zrm.block

import me.znepb.zrm.Registry
import me.znepb.zrm.block.entity.PostMountableBlockEntity
import me.znepb.zrm.block.entity.SignBlockEntity
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

open class PostMountableBlock<T : PostMountableBlockEntity>
    (settings: Settings, private val blockEntityFactory: BlockEntityFactory<T>, private val entityType: BlockEntityType<T>?)
    : BlockWithEntity(settings), BlockEntityProvider
{
    private var placementContext: ItemPlacementContext? = null

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, PostMountableBlockEntity.Companion::onTick)
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

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val be = blockEntityFactory.create(pos, state)
        this.placementContext?.let { be.setContext(it) }
        return be
    }
}