package me.znepb.zrm.block.cabinet

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class TrafficCabinet(settings: Settings) : BlockWithEntity(settings) {
    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return super.getPlacementState(ctx)!!.with(Properties.HORIZONTAL_FACING, ctx.horizontalPlayerFacing.opposite)
    }

    fun beforeBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        val blockEntity = world.getBlockEntity(pos)

        if(blockEntity is TrafficCabinetBlockEntity) {
            blockEntity.remove()
        }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        val be = TrafficCabinetBlockEntity(pos, state);
        return be
    }
}