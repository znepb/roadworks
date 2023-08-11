package me.znepb.zrm.block

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.enums.BlockHalf
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluids
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

open class DoubleHighConeBlock(settings: Settings) : HorizontalFacingBlock(settings) {
    companion object {
        val HALF = Properties.BLOCK_HALF
    }

    init {
        this.defaultState = this.defaultState.with(HALF, BlockHalf.BOTTOM).with(FACING, Direction.NORTH)
    }

    override fun getRenderType(state: BlockState): BlockRenderType {
        return if(state.get(HALF) == BlockHalf.TOP) BlockRenderType.INVISIBLE else BlockRenderType.MODEL
    }

    override fun isTransparent(state: BlockState, world: BlockView, pos: BlockPos): Boolean {
        return true
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess?,
        pos: BlockPos?,
        neighborPos: BlockPos?
    ): BlockState? {
        val doubleBlockHalf = state.get(HALF) as BlockHalf
        return if (
            direction.axis === Direction.Axis.Y
            && doubleBlockHalf == BlockHalf.BOTTOM == (direction == Direction.UP)
            && (!neighborState.isOf(this) || neighborState.get(HALF) == doubleBlockHalf)
        ) {
            Blocks.AIR.defaultState
        } else {
            if (doubleBlockHalf == BlockHalf.BOTTOM && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) Blocks.AIR.defaultState
            else super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        }
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val blockPos = ctx.blockPos
        val world = ctx.world
        return if (blockPos.y < world.topY - 1 && world.getBlockState(blockPos.up()).canReplace(ctx))
            super.getPlacementState(ctx)?.with(FACING, ctx.player?.horizontalFacing)
        else null
    }

    override fun onPlaced(
        world: World,
        pos: BlockPos,
        state: BlockState?,
        placer: LivingEntity?,
        itemStack: ItemStack?
    ) {
        val blockPos = pos.up()
        world.setBlockState(
            blockPos,
            defaultState.with(HALF, BlockHalf.TOP).with(FACING, state?.get(FACING)) as BlockState,
            NOTIFY_ALL
        )
    }

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        if (!world.isClient) {
            if (player.isCreative) {
                onBreakInCreative(world, pos, state, player)
            } else {
                dropStacks(state, world, pos, null as BlockEntity?, player, player.mainHandStack)
            }
        }
        super.onBreak(world, pos, state, player)
    }

    override fun afterBreak(
        world: World?,
        player: PlayerEntity?,
        pos: BlockPos?,
        state: BlockState?,
        blockEntity: BlockEntity?,
        tool: ItemStack?
    ) {
        super.afterBreak(world, player, pos, Blocks.AIR.defaultState, blockEntity, tool)
    }

    private fun onBreakInCreative(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity?) {
        val doubleBlockHalf = state.get(HALF) as BlockHalf
        if (doubleBlockHalf == BlockHalf.TOP) {
            val blockPos = pos.down()
            val blockState = world.getBlockState(blockPos)
            if (blockState.isOf(state.block) && blockState.get(HALF) == BlockHalf.BOTTOM) {
                val blockState2 =
                    if (blockState.fluidState.isOf(Fluids.WATER)) Blocks.WATER.defaultState else Blocks.AIR.defaultState
                world.setBlockState(blockPos, blockState2, SKIP_DROPS + NOTIFY_ALL)
                world.syncWorldEvent(player, 2001, blockPos, getRawIdFromState(blockState))
            }
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(*arrayOf<Property<*>>(HALF, FACING))
    }
}