package me.znepb.roadworks.block.marking

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.datagen.TagProvider
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import net.minecraft.block.*
import net.minecraft.data.client.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import java.util.*

abstract class AbstractMarking : HorizontalFacingBlock(Settings.copy(Blocks.WHITE_CONCRETE)) {
    companion object {
        val MARKING_SHAPE = createCuboidShape(0.0, 0.05, 0.0, 16.0, 0.1, 16.0)

        val basicMarkingModel = Model(
            Optional.of(RoadworksMain.ModId("block/marking_basic")), Optional.empty(),
            TextureKey.TEXTURE
        )

        fun addBasicMarking(generator: BlockStateModelGenerator, block: Block, id: String) {
            addBasicMarking(generator, block, id, false)
        }

        fun addBasicMarking(generator: BlockStateModelGenerator, block: Block, id: String, uvLock: Boolean?) {
            basicMarkingModel.upload(
                RoadworksMain.ModId("block/${id}"),
                TextureMap()
                    .put(TextureKey.TEXTURE, RoadworksMain.ModId("block/markings/$id")),
                generator.modelCollector
            )

            generator.blockStateCollector.accept(basicMarkingBlockStateSupplier(block, id, uvLock))
        }

        fun basicMarkingBlockStateSupplier(block: Block, id: String, uvLock: Boolean?): MultipartBlockStateSupplier {
            return OrientedBlockStateSupplier(
                RoadworksMain.ModId("block/$id"),
                { it },
                { it.put(VariantSettings.UVLOCK, uvLock) }
            ).put(MultipartBlockStateSupplier.create(block))
        }
    }

    override fun getCullingShape(state: BlockState, world: BlockView, pos: BlockPos): VoxelShape {
        return MARKING_SHAPE
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return MARKING_SHAPE
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return MARKING_SHAPE
    }

    protected abstract fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        val placement = super.getPlacementState(ctx)!!.with(Properties.HORIZONTAL_FACING, ctx.horizontalPlayerFacing)
        return getState(placement, ctx.world, ctx.blockPos)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if(canPlaceAt(state, world, pos)) getState(state, world, pos) else Blocks.AIR.defaultState
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val down = world.getBlockState(pos.down())
        return !down.isIn(TagProvider.MARKINGS) && !down.block.isTransparent(down, world, pos.down())
    }
}