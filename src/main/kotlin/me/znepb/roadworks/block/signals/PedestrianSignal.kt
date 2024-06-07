package me.znepb.roadworks.block.signals;

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class PedestrianSignal
    (settings: Settings)
    : AbstractTrafficSignal<PedestrianSignalBlockEntity>(settings, ::PedestrianSignalBlockEntity)
{
    companion object {
        val SIGNAL_SHAPE_WALL = createCuboidShape(4.0, 4.0, 13.0, 12.0, 12.0, 16.0)
        val SIGNAL_SHAPE_POST_NONE = createCuboidShape(4.0, 4.0, 7.0, 12.0, 12.0, 10.0)
        val SIGNAL_SHAPE_POST_THIN = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-2.0 / 16))
        val SIGNAL_SHAPE_POST_MEDIUM = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-3.0 / 16))
        val SIGNAL_SHAPE_POST_THICK = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-5.0 / 16))
    }

    override fun getAttachmentShape(world: BlockView, pos: BlockPos): VoxelShape {
        if(world.getBlockEntity(pos) !is PedestrianSignalBlockEntity) return VoxelShapes.empty();

        val blockEntity = world.getBlockEntity(pos) as PedestrianSignalBlockEntity

        return if(blockEntity.wall) {
            RotateVoxelShape.rotateVoxelShape(
                SIGNAL_SHAPE_WALL,
                Direction.NORTH,
                Direction.byId(blockEntity.facing)
            )
        } else {
            val maxThickness = AbstractPostMountableBlockEntity.getThickest(blockEntity)

            RotateVoxelShape.rotateVoxelShape(
                when(maxThickness) {
                    PostThickness.THIN -> SIGNAL_SHAPE_POST_THIN
                    PostThickness.MEDIUM -> SIGNAL_SHAPE_POST_MEDIUM
                    PostThickness.THICK -> SIGNAL_SHAPE_POST_THICK
                    else -> SIGNAL_SHAPE_POST_NONE
                },
                Direction.NORTH,
                Direction.byId(blockEntity.facing)
            )
        }
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(
            type,
            Registry.ModBlockEntities.PEDESTRIAN_SIGNAL_BLOCK_ENTITY,
            AbstractTrafficSignalBlockEntity.Companion::onTick
        )
    }
}