package me.znepb.zrm.block

import net.minecraft.block.Block
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.Equipment
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView

class TrafficCone(settings: Settings): Block(settings), Equipment {
    companion object {
        val SHAPE = createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?): Boolean {
        return true
    }

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun getCullingShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape {
        return SHAPE
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return createCuboidShape(2.0, 0.0, 2.0, 14.0, 24.0, 14.0)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPE
    }

    override fun getSlotType(): EquipmentSlot {
        return EquipmentSlot.HEAD
    }
}