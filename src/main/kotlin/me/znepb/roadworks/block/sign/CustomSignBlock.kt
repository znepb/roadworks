package me.znepb.roadworks.block.sign

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.post.AbstractPostMountableBlock
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape.Companion.rotateVoxelShape
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class CustomSignBlock(settings: Settings):
    AbstractPostMountableBlock<CustomSignBlockEntity>(settings, ::CustomSignBlockEntity) {

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.CUSTOM_SIGN_BLOCK_ENTITY, AbstractPostMountableBlockEntity.Companion::onTick)
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
        val blockEntity = world.getBlockEntity(pos) as CustomSignBlockEntity?
            ?: return VoxelShapes.empty()

        val width = (world.getBlockEntity(pos) as CustomSignBlockEntity).getContentsPixelWidth()

        var shape = createCuboidShape((8 - width / 2).toDouble(), 6.0, 15.5, (8 + width / 2).toDouble(), 10.0, 16.0)

        return if(blockEntity.wall) {
            rotateVoxelShape(shape, Direction.NORTH, Direction.byId(blockEntity.facing))
        } else {
            val maxThickness = AbstractPostMountableBlockEntity.getThickest(blockEntity)

            rotateVoxelShape(when(maxThickness) {
                PostThickness.THIN -> shape.offset(0.0, 0.0, (-8.75 / 16))
                PostThickness.MEDIUM -> shape.offset(0.0, 0.0, (-9.75 / 16))
                PostThickness.THICK -> shape.offset(0.0, 0.0, (-10.75 / 16))
                else -> shape.offset(0.0, 0.0, (-7.75 / 16))
            }, Direction.NORTH, Direction.byId(blockEntity.facing))
        }
    }

    override fun getPickStack(world: BlockView, pos: BlockPos, state: BlockState): ItemStack {
        val pickStack = super.getPickStack(world, pos, state)
        val blockEntity = world.getBlockEntity(pos)

        if(blockEntity is CustomSignBlockEntity) {
            val nbt = NbtCompound()
            blockEntity.writeExtraNBT(nbt)
            BlockItem.setBlockEntityNbt(pickStack, blockEntity.type, nbt)
        }

        return pickStack
    }
}