package me.znepb.roadworks.block.marking

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.logger
import me.znepb.roadworks.datagen.ModelProvider
import me.znepb.roadworks.util.MarkingUtil
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.data.client.*
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class TMarking : AbstractMarking() {
    companion object {
        val RIGHT_FILL = BooleanProperty.of("right_fill")
        val LEFT_FILL = BooleanProperty.of("left_fill")
        val BACK_FILL = BooleanProperty.of("back_fill")

        fun addTMarking(
            generator: BlockStateModelGenerator,
            block: Block,
            id: String,
            fillBack: String,
            fillLeft: String,
            fillRight: String,
            rotateBack: Boolean
        ) {
            basicMarkingModel.upload(
                RoadworksMain.ModId("block/$id"),
                TextureMap()
                    .put(TextureKey.TEXTURE, RoadworksMain.ModId("block/markings/$id")),
                generator.modelCollector
            )

            generator.blockStateCollector.accept(
                OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$fillBack"),
                    { it.set(BACK_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    if(rotateBack) 1 else 0
                ).put(OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$fillLeft"),
                    { it.set(LEFT_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    2
                ).put(OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$fillRight"),
                    { it.set(RIGHT_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    2
                ).put(basicMarkingBlockStateSupplier(block, id, false))))
            )
        }
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(RIGHT_FILL, false)
            .with(LEFT_FILL, false)
            .with(BACK_FILL, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(RIGHT_FILL)
        builder.add(LEFT_FILL)
        builder.add(BACK_FILL)
    }

    override fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        val facing = state.get(Properties.HORIZONTAL_FACING)

        // Right
        val right = world.getBlockState(pos.offset(facing.rotateYClockwise()))
        val front = world.getBlockState(pos.offset(facing.opposite))

        val rightState = if(right.block is OneSideFilledMarking && front.block is OneSideFilledMarking) {
            val rightFilled = MarkingUtil.getCardinalDirectionFilled(right, facing.opposite)
            val frontFilled = MarkingUtil.getCardinalDirectionFilled(front, facing.rotateYClockwise())

            rightFilled && frontFilled
        } else false

        // Left
        val left = world.getBlockState(pos.offset(facing.rotateYCounterclockwise()))

        val leftState = if(left.block is OneSideFilledMarking && front.block is OneSideFilledMarking) {
            val leftFilled = MarkingUtil.getCardinalDirectionFilled(left, facing.opposite)
            val frontFilled = MarkingUtil.getCardinalDirectionFilled(front, facing.rotateYCounterclockwise())

            leftFilled && frontFilled
        } else false

        // Back
        val back = world.getBlockState(pos.offset(facing))
        val connectBack = MarkingUtil.doesBorder(state, back)

        return state.with(LEFT_FILL, leftState).with(RIGHT_FILL, rightState).with(BACK_FILL, connectBack)
    }
}