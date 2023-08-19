package me.znepb.roadworks.block.signals

import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

abstract class AbstractThreeHeadSignal<T: AbstractTrafficSignalBlockEntity>
    (settings: Settings, blockEntityFactory: BlockEntityType.BlockEntityFactory<T>)
    : AbstractTrafficSignal<T>(settings, blockEntityFactory)
{
    companion object {
        val SIGNAL_SHAPE_WALL = createCuboidShape(5.0, 1.0, 15.0, 11.0, 15.0, 16.0)
        val SIGNAL_SHAPE_POST_NONE = createCuboidShape(5.0, 1.0, 7.0, 11.0, 15.0, 8.0)
        val SIGNAL_SHAPE_POST_THIN = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-1.0 / 16))
        val SIGNAL_SHAPE_POST_MEDIUM = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-2.0 / 16))
        val SIGNAL_SHAPE_POST_THICK = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-3.0 / 16))
    }

    override fun getAttachmentShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as AbstractTrafficSignalBlockEntity?
            ?: return VoxelShapes.empty()

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
}