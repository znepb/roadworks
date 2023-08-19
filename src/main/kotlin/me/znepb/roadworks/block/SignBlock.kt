package me.znepb.roadworks.block

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.block.post.AbstractPostMountableBlock
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape.Companion.rotateVoxelShape
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
    AbstractPostMountableBlock<SignBlockEntity>(settings, ::SignBlockEntity) {

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
        return checkType(type, Registry.ModBlockEntities.SIGN_BLOCK_ENTITY, AbstractPostMountableBlockEntity.Companion::onTick)
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

    override fun getAttachmentShape(world: BlockView, pos: BlockPos): VoxelShape {
        val blockEntity = world.getBlockEntity(pos) as SignBlockEntity?
            ?: return VoxelShapes.empty()

        return if(blockEntity.wall) {
            rotateVoxelShape(SIGN_SHAPE_WALL, Direction.NORTH, Direction.byId(blockEntity.facing))
        } else {
            val maxThickness = AbstractPostMountableBlockEntity.getThickest(blockEntity)

            rotateVoxelShape(when(maxThickness) {
                PostThickness.THIN -> SIGN_SHAPE_POST_THIN
                PostThickness.MEDIUM -> SIGN_SHAPE_POST_MEDIUM
                PostThickness.THICK -> SIGN_SHAPE_POST_THICK
                else -> SIGN_SHAPE_POST_NONE
            }, Direction.NORTH, Direction.byId(blockEntity.facing))
        }
    }
}