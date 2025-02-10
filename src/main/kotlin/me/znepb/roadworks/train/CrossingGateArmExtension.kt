package me.znepb.roadworks.train

import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.container.PostContainerBlockEntity
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape.Companion.rotateVoxelShape
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.StringIdentifiable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView

class CrossingGateArmExtension : HorizontalFacingBlock(Settings.copy(Blocks.WHITE_CONCRETE)) {
    companion object {
        val THICKNESS: EnumProperty<PostThickness> = EnumProperty.of("thickness", PostThickness::class.java)
        val DIRECTION: EnumProperty<CrossingArmDirection> = EnumProperty.of("direction", CrossingArmDirection::class.java)

        val HORIZONTAL_ARM_SHAPE = VoxelShapes.cuboid( 0.0, 3.25 / 16.0, 7.5 / 16.0, 16.0 / 16.0, 5.25 / 16.0, 8.5 / 16.0)
        val VERTICAL_ARM_SHAPE = VoxelShapes.cuboid( 10.75 / 16.0, 0.0, 7.5 / 16.0, 12.75 / 16.0, 16.0 / 16.0, 8.5 / 16.0)
    }

    init {
        defaultState = defaultState.with(THICKNESS, PostThickness.NONE)
            .with(DIRECTION, CrossingArmDirection.HORIZONTAL)
            .with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val placedOn = ctx.blockPos.offset(ctx.side.opposite)
        logger.info(placedOn.toString())
        val be = ctx.world.getBlockEntity(placedOn)
        var thickness = PostThickness.NONE
        var beDirection: Direction? = null
        var direction = CrossingArmDirection.HORIZONTAL

        if(be is PostContainerBlockEntity) {
            thickness = be.thickness
            val hitAttachment = be.attachments.find {
                it is CrossingGateAttachment
            }

            if(hitAttachment is CrossingGateAttachment) {
                beDirection = hitAttachment.facing
                direction = if(hitAttachment.isActive()) CrossingArmDirection.HORIZONTAL else CrossingArmDirection.VERTICAL
            }
        } else if(ctx.world.getBlockState(placedOn).isOf(RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION)) {
            thickness = ctx.world.getBlockState(placedOn).get(THICKNESS)
            beDirection = ctx.world.getBlockState(placedOn).get(FACING)
            direction = ctx.world.getBlockState(placedOn).get(DIRECTION)
        }

        return super.getPlacementState(ctx)!!
            .with(FACING, beDirection ?: ctx.horizontalPlayerFacing.opposite)
            .with(THICKNESS, thickness)
            .with(DIRECTION, direction)
    }

    fun getShape(state: BlockState): VoxelShape {
        val offset = when(state.get(THICKNESS)) {
            PostThickness.THICK -> 4.0 / 16
            PostThickness.MEDIUM -> 3.0 / 16
            PostThickness.THIN -> 2.0 / 16
            PostThickness.NONE -> 0.0
            null -> 0.0
        }

        return when(state.get(DIRECTION)) {
            CrossingArmDirection.VERTICAL -> rotateVoxelShape(VERTICAL_ARM_SHAPE.offset(0.0, 0.0, -offset), Direction.NORTH, state.get(FACING))
            CrossingArmDirection.HORIZONTAL -> rotateVoxelShape(HORIZONTAL_ARM_SHAPE.offset(0.0, 0.0, -offset), Direction.NORTH, state.get(FACING))
            null -> VoxelShapes.empty()
        }
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return getShape(state)
    }

    override fun getCullingShape(state: BlockState, world: BlockView?, pos: BlockPos?): VoxelShape {
        return getShape(state)
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape {
        return getShape(state)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(THICKNESS)
        builder.add(DIRECTION)
    }

    enum class CrossingArmDirection : StringIdentifiable {
        VERTICAL,
        HORIZONTAL;

        override fun toString(): String {
            return asString()
        }

        override fun asString(): String {
            return this.name.lowercase()
        }
    }
}