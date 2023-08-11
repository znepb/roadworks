package me.znepb.zrm.block

import me.znepb.zrm.Registry
import me.znepb.zrm.block.PostBlock.Companion.getShapeFromDirectionAndSize
import me.znepb.zrm.block.entity.PostMountableBlockEntity
import me.znepb.zrm.block.entity.SignBlockEntity
import me.znepb.zrm.util.PostThickness
import me.znepb.zrm.util.RotateVoxelShape.Companion.rotateVoxelShape
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class SignBlock(settings: Settings, val frontTexture: String, val backTexture: String):
    PostMountableBlock<SignBlockEntity>(settings, ::SignBlockEntity, Registry.ModBlockEntities.SIGN_BLOCK_ENTITY) {

    companion object {
        val SIGN_SHAPE_WALL = createCuboidShape(0.0, 0.0, 15.5, 16.0, 16.0, 16.0)
        val SIGN_SHAPE_POST_NONE = SIGN_SHAPE_WALL.offset(0.0, 0.0, (-7.75 / 16))
        val SIGN_SHAPE_POST_THIN = SIGN_SHAPE_WALL.offset(0.0, 0.0, (-8.75 / 16))
        val SIGN_SHAPE_POST_MEDIUM = SIGN_SHAPE_WALL.offset(0.0, 0.0, (-9.75 / 16))
        val SIGN_SHAPE_POST_THICK = SIGN_SHAPE_WALL.offset(0.0, 0.0, (-10.75 / 16))
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, PostMountableBlockEntity.Companion::onTick)
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
            PostThickness.THICK -> getShapeFromDirectionAndSize(direction, PostThickness.THICK)
            PostThickness.MEDIUM -> getShapeFromDirectionAndSize(direction, PostThickness.MEDIUM)
            PostThickness.THIN -> getShapeFromDirectionAndSize(direction, PostThickness.THIN)
            else -> VoxelShapes.empty()
        }
    }

    private fun getShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as SignBlockEntity?
            ?: return VoxelShapes.empty()

        var signShape: VoxelShape;

        if(blockEntity.wall) {
            signShape = rotateVoxelShape(SIGN_SHAPE_WALL, Direction.NORTH, Direction.byId(blockEntity.facing))
        } else {
            val maxThickness = PostMountableBlockEntity.getThickest(blockEntity)

            signShape = when(maxThickness) {
                PostThickness.THIN -> rotateVoxelShape(SIGN_SHAPE_POST_THIN, Direction.NORTH, Direction.byId(blockEntity.facing))
                PostThickness.MEDIUM -> rotateVoxelShape(SIGN_SHAPE_POST_MEDIUM, Direction.NORTH, Direction.byId(blockEntity.facing))
                PostThickness.THICK -> rotateVoxelShape(SIGN_SHAPE_POST_THICK, Direction.NORTH, Direction.byId(blockEntity.facing))
                else -> rotateVoxelShape(SIGN_SHAPE_POST_NONE, Direction.NORTH, Direction.byId(blockEntity.facing))
            }

            signShape = VoxelShapes.union(
                signShape,
                when(maxThickness) {
                    PostThickness.THIN -> PostBlock.MIDSECTION_SHAPE_THIN
                    PostThickness.MEDIUM -> PostBlock.MIDSECTION_SHAPE_MEDIUM
                    PostThickness.THICK -> PostBlock.MIDSECTION_SHAPE_THICK
                    else -> VoxelShapes.empty()
                }
            )

            Direction.entries.forEach {
                signShape = VoxelShapes.union(signShape, pickSideShape(blockEntity.getDirectionThickness(it), it))
            }
        }

        return signShape
    }
}