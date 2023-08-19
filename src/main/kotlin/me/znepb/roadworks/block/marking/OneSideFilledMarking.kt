package me.znepb.roadworks.block.marking

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.datagen.TagProvider
import me.znepb.roadworks.util.MarkingUtil.Companion.doesBorder
import me.znepb.roadworks.util.MarkingUtil.Companion.getAbsoluteFromRelative
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import me.znepb.roadworks.util.Side
import net.fabricmc.fabric.impl.tag.convention.TagRegistration
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks.WHITE_CONCRETE
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.ShapeContext
import net.minecraft.data.client.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess
import java.util.*

class OneSideFilledMarking : AbstractMarking() {
    companion object {
        val RIGHT_FILL = BooleanProperty.of("right_fill")
        val LEFT_FILL = BooleanProperty.of("left_fill")

        fun addMarkingWithFilledSides(generator: BlockStateModelGenerator, block: Block, id: String, leftModel: String, rightModel: String) {
            basicMarkingModel.upload(
                RoadworksMain.ModId("block/$id"),
                TextureMap()
                    .put(TextureKey.TEXTURE, RoadworksMain.ModId("block/markings/$id")),
                generator.modelCollector
            )

            generator.blockStateCollector.accept(
                OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$leftModel"),
                    { it.set(LEFT_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) }
                ).put(OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$rightModel"),
                    { it.set(RIGHT_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    2
                ).put(
                    basicMarkingBlockStateSupplier(block, id, false)
                ))
            )
        }
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(RIGHT_FILL, false)
            .with(LEFT_FILL, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(RIGHT_FILL)
        builder.add(LEFT_FILL)
    }

     override fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        val directionRight = getAbsoluteFromRelative(state, Side.RIGHT)
        val directionLeft = directionRight.opposite

        val blockLeft = world.getBlockState(pos.offset(directionLeft))
        val blockRight = world.getBlockState(pos.offset(directionRight))

        val connectLeft = doesBorder(state, blockLeft)
        val connectRight = doesBorder(state, blockRight)

        return state.with(RIGHT_FILL, connectRight).with(LEFT_FILL, connectLeft)
    }
}