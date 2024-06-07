package me.znepb.roadworks.block.marking

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.util.MarkingUtil.Companion.doesBorder
import me.znepb.roadworks.util.MarkingUtil.Companion.getAbsoluteFromRelative
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import me.znepb.roadworks.util.Side
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.client.VariantSettings
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class OneSideFilledMarking : AbstractMarking() {
    companion object {
        val RIGHT_FILL = BooleanProperty.of("right_fill")
        val LEFT_FILL = BooleanProperty.of("left_fill")
        val SHOULD_FILL = BooleanProperty.of("should_fill")

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

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        if(ctx.player !== null) {
            return getState(
                super.getPlacementState(ctx).with(
                    SHOULD_FILL, !ctx.player!!.isSneaking
                ), ctx.world, ctx.blockPos)
        }

        return getState(super.getPlacementState(ctx), ctx.world, ctx.blockPos)
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(RIGHT_FILL, false)
            .with(LEFT_FILL, false)
            .with(SHOULD_FILL, true)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(RIGHT_FILL)
        builder.add(LEFT_FILL)
        builder.add(SHOULD_FILL)
    }

     override fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
         if(!state.get(SHOULD_FILL)) {
             return state.with(RIGHT_FILL, false).with(LEFT_FILL, false)
         }

        val directionRight = getAbsoluteFromRelative(state, Side.RIGHT)
        val directionLeft = directionRight.opposite

        val blockLeft = world.getBlockState(pos.offset(directionLeft))
        val blockRight = world.getBlockState(pos.offset(directionRight))

        val connectLeft = doesBorder(state, blockLeft)
        val connectRight = doesBorder(state, blockRight)

        return state.with(RIGHT_FILL, connectRight).with(LEFT_FILL, connectLeft)
    }
}