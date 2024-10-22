package me.znepb.roadworks.block.marking

import me.znepb.roadworks.Registry
import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.util.MarkingUtil.Companion.getCardinalDirectionFilled
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.data.client.TextureKey
import net.minecraft.data.client.TextureMap
import net.minecraft.data.client.VariantSettings
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

class TurnMarking(private val mirror: Boolean = false) : AbstractMarking() {
    companion object {
        val INSIDE_FILL = BooleanProperty.of("inside_fill")
        val OUTSIDE_FILL = BooleanProperty.of("outside_fill")

        fun addTurnMarking(generator: BlockStateModelGenerator, block: Block, id: String, fillModelInside: String, fillModelOutside: String) {
            basicMarkingModel.upload(
                RoadworksMain.ModId("block/$id"),
                TextureMap()
                    .put(TextureKey.TEXTURE, RoadworksMain.ModId("block/markings/$id")),
                generator.modelCollector
            )

            generator.blockStateCollector.accept(
                OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$fillModelOutside"),
                    { it.set(OUTSIDE_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    2
                ).put(OrientedBlockStateSupplier(
                    RoadworksMain.ModId("block/$fillModelInside"),
                    { it.set(INSIDE_FILL, true) },
                    { it.put(VariantSettings.UVLOCK, true) },
                    2
                ).put(basicMarkingBlockStateSupplier(block, id, false)))
            )
        }
    }

    init {
        defaultState = defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
            .with(INSIDE_FILL, false)
            .with(OUTSIDE_FILL, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(Properties.HORIZONTAL_FACING)
        builder.add(INSIDE_FILL)
        builder.add(OUTSIDE_FILL)
    }

    override fun getState(state: BlockState, world: WorldAccess, pos: BlockPos): BlockState {
        val facing = state.get(Properties.HORIZONTAL_FACING)

        // Inside
        val dir = if(mirror) facing.rotateYCounterclockwise() else facing.rotateYClockwise()
        val side = world.getBlockState(pos.offset(dir))
        val back = world.getBlockState(pos.offset(facing.opposite))

        val inside = if(side.block is OneSideFilledMarking && back.block is OneSideFilledMarking) {
            val rightFilled = getCardinalDirectionFilled(side, facing.opposite)
            val backFilled = getCardinalDirectionFilled(back, dir)

            rightFilled && backFilled
        } else false

        // outside

        val outsideSide = world.getBlockState(pos.offset(dir.opposite))
        val front = world.getBlockState(pos.offset(facing))

        val outside = if(outsideSide.block is OneSideFilledMarking && front.block is OneSideFilledMarking) {
            val leftFilled = getCardinalDirectionFilled(outsideSide, dir.opposite)
            val frontFilled = getCardinalDirectionFilled(front, facing.opposite)

            leftFilled && frontFilled
        } else outsideSide.isOf(Registry.ModBlocks.WHITE_INFILL_MARKING) && front.isOf(Registry.ModBlocks.WHITE_INFILL_MARKING)

        return state.with(INSIDE_FILL, inside).with(OUTSIDE_FILL, outside)
    }
}