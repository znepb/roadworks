package me.znepb.roadworks.misc

import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.util.PostThickness
import me.znepb.roadworks.util.RotateVoxelShape.Companion.rotateVoxelShape
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

class Catwalk : Block(Settings.copy(Blocks.IRON_BLOCK)) {
    fun canConnect(blockState: BlockState) = blockState.isOf(RoadworksRegistry.ModBlocks.CATWALK)

    companion object {
        val THICKNESS: EnumProperty<PostThickness> = EnumProperty.of("thickness", PostThickness::class.java)
        val BOTTOM = VoxelShapes.cuboid(0.0, 0.0 / 16.0, 0.0, 16.0 / 16.0, 2.0 / 16.0, 16.0 / 16.0)
        val RAILING_WEST = VoxelShapes.cuboid(0.0, 2.0 / 16.0, 0.0, 1.0 / 16.0, 16.0 / 16.0, 16.0 / 16.0)
        val RAILING_NORTH = rotateVoxelShape(RAILING_WEST, Direction.WEST, Direction.NORTH)
        val RAILING_EAST = rotateVoxelShape(RAILING_WEST, Direction.WEST, Direction.EAST)
        val RAILING_SOUTH = rotateVoxelShape(RAILING_WEST, Direction.WEST, Direction.SOUTH)

        val DEVICE_NORTH = BooleanProperty.of("device_north")
        val DEVICE_EAST = BooleanProperty.of("device_east")
        val DEVICE_SOUTH = BooleanProperty.of("device_south")
        val DEVICE_WEST = BooleanProperty.of("device_west")
    }

    override fun getCullingShape(state: BlockState?, world: BlockView?, pos: BlockPos?): VoxelShape {
        return VoxelShapes.empty()
    }

    override fun getCollisionShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.union(BOTTOM,
            if(!state.get(HorizontalConnectingBlock.NORTH)) RAILING_NORTH else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.EAST)) RAILING_EAST else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.SOUTH)) RAILING_SOUTH else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.WEST)) RAILING_WEST else VoxelShapes.empty()
        )
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return VoxelShapes.union(BOTTOM,
            if(!state.get(HorizontalConnectingBlock.NORTH)) RAILING_NORTH else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.EAST)) RAILING_EAST else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.SOUTH)) RAILING_SOUTH else VoxelShapes.empty(),
            if(!state.get(HorizontalConnectingBlock.WEST)) RAILING_WEST else VoxelShapes.empty()
        )
    }

    fun evaluateState(world: BlockView, pos: BlockPos) : BlockState {
        val stateNorth = world.getBlockState(pos.north())
        val stateEast = world.getBlockState(pos.east())
        val stateSouth = world.getBlockState(pos.south())
        val stateWest = world.getBlockState(pos.west())
        val be = world.getBlockEntity(pos.down(), RoadworksRegistry.ModBlockEntities.POST_CONTAINER_BLOCK_ENTITY)

        return this.defaultState
            .with(HorizontalConnectingBlock.NORTH, canConnect(stateNorth))
            .with(HorizontalConnectingBlock.EAST, canConnect(stateEast))
            .with(HorizontalConnectingBlock.SOUTH, canConnect(stateSouth))
            .with(HorizontalConnectingBlock.WEST, canConnect(stateWest))
            .with(THICKNESS, if(be.isPresent) be.get().thickness else PostThickness.NONE)
            .with(DEVICE_NORTH, false)
            .with(DEVICE_EAST, false)
            .with(DEVICE_SOUTH, false)
            .with(DEVICE_WEST, false)

    }

    override fun isTransparent(state: BlockState?, world: BlockView?, pos: BlockPos?) = true

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return evaluateState(ctx.world, ctx.blockPos)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(HorizontalConnectingBlock.NORTH)
        builder.add(HorizontalConnectingBlock.EAST)
        builder.add(HorizontalConnectingBlock.SOUTH)
        builder.add(HorizontalConnectingBlock.WEST)
        builder.add(THICKNESS)
        builder.add(DEVICE_NORTH)
        builder.add(DEVICE_EAST)
        builder.add(DEVICE_SOUTH)
        builder.add(DEVICE_WEST)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return evaluateState(world, pos)
    }
}