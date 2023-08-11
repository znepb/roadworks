package me.znepb.zrm.block.signals

import me.znepb.zrm.Registry
import me.znepb.zrm.block.PostBlock
import me.znepb.zrm.block.PostMountableBlock
import me.znepb.zrm.block.SignBlock
import me.znepb.zrm.block.entity.PostMountableBlockEntity
import me.znepb.zrm.block.entity.SignBlockEntity
import me.znepb.zrm.block.entity.signals.ThreeHeadTrafficSignalBlockEntity
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RotateVoxelShape
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class ThreeHeadTrafficSignal(settings: Settings) :
    PostMountableBlock<ThreeHeadTrafficSignalBlockEntity>(settings, ::ThreeHeadTrafficSignalBlockEntity, Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY)
{
    companion object {
        val SIGNAL_SHAPE_WALL = createCuboidShape(5.0, 1.0, 15.0, 11.0, 15.0, 16.0)
        val SIGNAL_SHAPE_POST_NONE = createCuboidShape(5.0, 1.0, 7.0, 11.0, 15.0, 8.0)
        val SIGNAL_SHAPE_POST_THIN = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-1.0 / 16))
        val SIGNAL_SHAPE_POST_MEDIUM = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-2.0 / 16))
        val SIGNAL_SHAPE_POST_THICK = SIGNAL_SHAPE_POST_NONE.offset(0.0, 0.0, (-3.0 / 16))
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY, ThreeHeadTrafficSignalBlockEntity.Companion::onTick)
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

    private fun pickSideShape(connectingSize: PostThickness, direction: Direction): VoxelShape {
        return when(connectingSize) {
            PostThickness.THICK -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.THICK)
            PostThickness.MEDIUM -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.MEDIUM)
            PostThickness.THIN -> PostBlock.getShapeFromDirectionAndSize(direction, PostThickness.THIN)
            else -> VoxelShapes.empty()
        }
    }

    private fun getShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as ThreeHeadTrafficSignalBlockEntity?
            ?: return VoxelShapes.empty()

        var signalShape: VoxelShape

        if(blockEntity.wall) {
            signalShape = RotateVoxelShape.rotateVoxelShape(
                SIGNAL_SHAPE_WALL,
                Direction.NORTH,
                Direction.byId(blockEntity.facing)
            )
        } else {
            val maxThickness = PostMountableBlockEntity.getThickest(blockEntity)

            signalShape = when(maxThickness) {
                PostThickness.THIN -> RotateVoxelShape.rotateVoxelShape(
                    SIGNAL_SHAPE_POST_THIN,
                    Direction.NORTH,
                    Direction.byId(blockEntity.facing)
                )
                PostThickness.MEDIUM -> RotateVoxelShape.rotateVoxelShape(
                    SIGNAL_SHAPE_POST_MEDIUM,
                    Direction.NORTH,
                    Direction.byId(blockEntity.facing)
                )
                PostThickness.THICK -> RotateVoxelShape.rotateVoxelShape(
                    SIGNAL_SHAPE_POST_THICK,
                    Direction.NORTH,
                    Direction.byId(blockEntity.facing)
                )
                else -> RotateVoxelShape.rotateVoxelShape(
                    SIGNAL_SHAPE_POST_NONE,
                    Direction.NORTH,
                    Direction.byId(blockEntity.facing)
                )
            }

            signalShape = VoxelShapes.union(
                signalShape,
                when(maxThickness) {
                    PostThickness.THIN -> PostBlock.MIDSECTION_SHAPE_THIN
                    PostThickness.MEDIUM -> PostBlock.MIDSECTION_SHAPE_MEDIUM
                    PostThickness.THICK -> PostBlock.MIDSECTION_SHAPE_THICK
                    else -> VoxelShapes.empty()
                }
            )

            Direction.entries.forEach {
                signalShape = VoxelShapes.union(signalShape, pickSideShape(blockEntity.getDirectionThickness(it), it))
            }
        }

        return signalShape
    }
}