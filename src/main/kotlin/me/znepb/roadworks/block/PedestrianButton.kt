package me.znepb.roadworks.block

import me.znepb.roadworks.Registry
import me.znepb.roadworks.block.post.AbstractPostMountableBlock
import me.znepb.roadworks.block.post.AbstractPostMountableBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.event.GameEvent

class PedestrianButton(settings: Settings) : AbstractPostMountableBlock<PedestrianButtonBlockEntity>(settings, ::PedestrianButtonBlockEntity)
{
    init {
        defaultState = defaultState.with(Properties.POWERED, false)
    }

    companion object {
        val SHAPE_POST_NONE = createCuboidShape(6.0, 6.0, 6.5, 10.0, 10.0, 9.0)
        val SHAPE_WALL = SHAPE_POST_NONE.offset(0.0, 0.0, (7.0 / 16))
        val SHAPE_POST_THIN = SHAPE_POST_NONE.offset(0.0, 0.0, (-2.0 / 16))
        val SHAPE_POST_MEDIUM = SHAPE_POST_NONE.offset(0.0, 0.0, (-3.0 / 16))
        val SHAPE_POST_THICK = SHAPE_POST_NONE.offset(0.0, 0.0, (-3.0 / 16))
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult? {
        return if (state.get(Properties.POWERED) as Boolean) {
            ActionResult.CONSUME
        } else {
            world.setBlockState(pos, state.with(Properties.POWERED, true) as BlockState, Block.NOTIFY_LISTENERS + Block.NOTIFY_NEIGHBORS)
            world.scheduleBlockTick(pos, this, 10)
            world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos)
            this.blockEntity?.press()
            ActionResult.success(world.isClient)
        }
    }

    override fun scheduledTick(state: BlockState, world: ServerWorld, pos: BlockPos?, random: Random?) {
        if (state.get(Properties.POWERED) as Boolean) {
            world.setBlockState(pos, state.with(Properties.POWERED, false) as BlockState, Block.NOTIFY_LISTENERS + Block.NOTIFY_NEIGHBORS)
        }
    }

    override fun getWeakRedstonePower(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        direction: Direction?
    ): Int {
        return if (state.get(Properties.POWERED) as Boolean) 15 else 0
    }

    override fun getStrongRedstonePower(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        direction: Direction
    ): Int {
        return if (state.get(Properties.POWERED) as Boolean && this.blockEntity?.facing?.let {
                Direction.fromHorizontal(
                    it
                )
            } == direction) 15 else 0
    }

    override fun emitsRedstonePower(state: BlockState?): Boolean {
        return true
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(*arrayOf(Properties.POWERED))
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (world.isClient) return null
        return checkType(type, Registry.ModBlockEntities.PEDESTRIAN_BUTTON_BLOCK_ENTITY, AbstractPostMountableBlockEntity.Companion::onTick)
    }

    override fun getAttachmentShape(world: BlockView, pos: BlockPos): VoxelShape {
        if(world.getBlockEntity(pos) !is PedestrianButtonBlockEntity) return VoxelShapes.empty()

        val blockEntity = world.getBlockEntity(pos) as PedestrianButtonBlockEntity

        return if(blockEntity.wall) {
            RotateVoxelShape.rotateVoxelShape(
                SHAPE_WALL,
                Direction.NORTH,
                Direction.byId(blockEntity.facing)
            )
        } else {
            val maxThickness = AbstractPostMountableBlockEntity.getThickest(blockEntity)

            RotateVoxelShape.rotateVoxelShape(
                when(maxThickness) {
                    PostThickness.THIN -> SHAPE_POST_THIN
                    PostThickness.MEDIUM -> SHAPE_POST_MEDIUM
                    PostThickness.THICK -> SHAPE_POST_THICK
                    else -> SHAPE_POST_NONE
                },
                Direction.NORTH,
                Direction.byId(blockEntity.facing)
            )
        }
    }
}