package me.znepb.zrm.datagen

import me.znepb.zrm.Main.ModId
import me.znepb.zrm.Registry
import me.znepb.zrm.block.SignBlock
import me.znepb.zrm.block.marking.OneSideFilledMarking
import me.znepb.zrm.block.marking.TurnMarking
import me.znepb.zrm.block.signals.SignalLight
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.block.enums.BlockHalf
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createSingletonBlockState
import net.minecraft.data.client.VariantSettings.Rotation
import net.minecraft.registry.RegistryOps.RegistryInfoGetter
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    companion object {
        val signModel = Model(
            Optional.of(ModId("block/sign_base")), Optional.empty(),
            TextureKey.FRONT,
            TextureKey.BACK
        )

        val signalModel = Model(
            Optional.of(ModId("block/signal")), Optional.empty(),
            TextureKey.TEXTURE
        )

        val basicMarkingModel = Model(
            Optional.of(ModId("block/marking_basic")), Optional.empty(),
            TextureKey.TEXTURE
        )

        val signals = getSignalTextures()

        private fun getSignalTextures(): List<String> {
            val list = mutableListOf<String>();

            SignalLight.entries.forEach {
                list.add("${it.light}_on")
                list.add("${it.light}_off")
            }

            return list
        }
    }

    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {
        addPole(generator, Registry.ModBlocks.POST, "post")
        addPole(generator, Registry.ModBlocks.THICK_POST, "thick_post")
        addPole(generator, Registry.ModBlocks.THIN_POST, "thin_post")

        addSign(generator, Registry.ModBlocks.STOP_SIGN, "stop_sign")
        addSign(generator, Registry.ModBlocks.STOP_SIGN_4_WAY, "stop_sign_4_way")
        addSign(generator, Registry.ModBlocks.STOP_SIGN_AHEAD, "stop_ahead_sign")
        addSign(generator, Registry.ModBlocks.YIELD_SIGN, "yield_sign")
        addSign(generator, Registry.ModBlocks.YIELD_SIGN_AHEAD, "yield_ahead_sign")
        addSign(generator, Registry.ModBlocks.SIGNAL_AHEAD, "signal_ahead_sign")
        addSign(generator, Registry.ModBlocks.ROAD_WORK_AHEAD, "road_work_ahead_sign")

        addDoubleHighConeBlock(generator, Registry.ModBlocks.CHANNELER, "channeler")
        addDoubleHighConeBlock(generator, Registry.ModBlocks.DRUM, "drum")

        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL, ModId("block/three_head_traffic_signal")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT, ModId("block/three_head_traffic_signal_left")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT, ModId("block/three_head_traffic_signal_right")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT, ModId("block/three_head_traffic_signal_straight")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT, ModId("block/five_head_traffic_signal_left")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT, ModId("block/five_head_traffic_signal_right")))

        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.TRAFFIC_CONE, ModId( "block/traffic_cone")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.BOLLARD_THIN, ModId("block/bollard_thin")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.BOLLARD, ModId("block/bollard")))
        generator.blockStateCollector.accept(createSingletonBlockState(Registry.ModBlocks.BOLLARD_THICK, ModId("block/bollard_thick")))

        TexturedModel.ORIENTABLE_WITH_BOTTOM
            .get(Registry.ModBlocks.TRAFFIC_CABINET)
            .textures{ m ->
                m.put(TextureKey.TOP, TextureMap.getSubId(Registry.ModBlocks.TRAFFIC_CABINET, "_top"))
                m.put(TextureKey.BOTTOM, TextureMap.getSubId(Registry.ModBlocks.TRAFFIC_CABINET, "_bottom"))
                m.put(TextureKey.SIDE, TextureMap.getSubId(Registry.ModBlocks.TRAFFIC_CABINET, "_side"))
                m.put(TextureKey.FRONT, TextureMap.getSubId(Registry.ModBlocks.TRAFFIC_CABINET, "_front"))
            }
            .upload(Registry.ModBlocks.TRAFFIC_CABINET, "", generator.modelCollector)

        generator.blockStateCollector.accept(MultipartBlockStateSupplier.create(Registry.ModBlocks.TRAFFIC_CABINET)
            .with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.NORTH),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/traffic_cabinet"))
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.EAST),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/traffic_cabinet"))
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/traffic_cabinet"))
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.WEST),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/traffic_cabinet"))
            )
        )

        addMarkingWithFilledSides(generator, Registry.ModBlocks.WHITE_CENTER_MARKING, "marking_white_center", "marking_white_center_filled")
        addBasicMarking(generator, Registry.ModBlocks.WHITE_INFILL_MARKING, "marking_white_infill", true)
        addBasicMarking(generator, Registry.ModBlocks.WHITE_CENTER_DASH_MARKING, "marking_white_center_dash")
        addTurnMarking(generator, Registry.ModBlocks.WHITE_CENTER_TURN_MARKING, "marking_white_turn_center", "marking_white_turn_center_filled_inside", "marking_white_turn_center_filled_outside")

        signals.forEach { addSignal(generator, it) }
    }

    private fun addBasicMarking(generator: BlockStateModelGenerator, block: Block, id: String) {
        addBasicMarking(generator, block, id, false)
    }

    private fun addBasicMarking(generator: BlockStateModelGenerator, block: Block, id: String, uvLock: Boolean?) {
        basicMarkingModel.upload(
            ModId("block/${id}"),
            TextureMap()
                .put(TextureKey.TEXTURE, Identifier("zrm", "block/markings/$id")),
            generator.modelCollector
        )

        generator.blockStateCollector.accept(basicMarkingBlockStateSupplier(block, id, uvLock))
    }

    private fun basicMarkingBlockStateSupplier(block: Block, id: String, uvLock: Boolean?): MultipartBlockStateSupplier {
        return MultipartBlockStateSupplier.create(block)
            .with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.NORTH),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/$id"))
                    .put(VariantSettings.UVLOCK, uvLock)
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.EAST),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/$id"))
                    .put(VariantSettings.UVLOCK, uvLock)
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.SOUTH),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/$id"))
                    .put(VariantSettings.UVLOCK, uvLock)
            ).with(
                When.create().set(Properties.HORIZONTAL_FACING, Direction.WEST),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/$id"))
                    .put(VariantSettings.UVLOCK, uvLock)
            )
    }

    private fun addMarkingWithFilledSides(generator: BlockStateModelGenerator, block: Block, id: String, fillModel: String) {
        basicMarkingModel.upload(
            ModId("block/$id"),
            TextureMap()
                .put(TextureKey.TEXTURE, Identifier("zrm", "block/markings/$id")),
            generator.modelCollector
        )

        // there HAS to be a better way to do this, if there is please PR it :D
        generator.blockStateCollector.accept(basicMarkingBlockStateSupplier(block, id, false).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.NORTH)
                    .set(OneSideFilledMarking.RIGHT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.EAST)
                    .set(OneSideFilledMarking.RIGHT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.SOUTH)
                    .set(OneSideFilledMarking.RIGHT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.WEST)
                    .set(OneSideFilledMarking.RIGHT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.NORTH)
                    .set(OneSideFilledMarking.LEFT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.EAST)
                    .set(OneSideFilledMarking.LEFT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.SOUTH)
                    .set(OneSideFilledMarking.LEFT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.WEST)
                    .set(OneSideFilledMarking.LEFT_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/$fillModel"))
                    .put(VariantSettings.UVLOCK, true)
            )
        )
    }

    private fun addTurnMarking(generator: BlockStateModelGenerator, block: Block, id: String, fillModelInside: String, fillModelOutside: String) {
        basicMarkingModel.upload(
            ModId("block/$id"),
            TextureMap()
                .put(TextureKey.TEXTURE, Identifier("zrm", "block/markings/$id")),
            generator.modelCollector
        )

        // there HAS to be a better way to do this, if there is please PR it :D
        generator.blockStateCollector.accept(basicMarkingBlockStateSupplier(block, id, false).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.NORTH)
                    .set(TurnMarking.INSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelInside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.EAST)
                    .set(TurnMarking.INSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelInside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.SOUTH)
                    .set(TurnMarking.INSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelInside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.WEST)
                    .set(TurnMarking.INSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelInside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.NORTH)
                    .set(TurnMarking.OUTSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R180)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelOutside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.EAST)
                    .set(TurnMarking.OUTSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R270)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelOutside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.SOUTH)
                    .set(TurnMarking.OUTSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R0)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelOutside"))
                    .put(VariantSettings.UVLOCK, true)
            ).with(
                When.create()
                    .set(Properties.HORIZONTAL_FACING, Direction.WEST)
                    .set(TurnMarking.OUTSIDE_FILL, true),
                BlockStateVariant.create()
                    .put(VariantSettings.Y, Rotation.R90)
                    .put(VariantSettings.MODEL, ModId("block/$fillModelOutside"))
                    .put(VariantSettings.UVLOCK, true)
            )
        )
    }

    private fun addSignal(generator: BlockStateModelGenerator, signalName: String) {
        signalModel.upload(
            ModId("block/signal_$signalName"),
            TextureMap()
                .put(TextureKey.TEXTURE, Identifier("zrm", "block/signals/${signalName}")),
            generator.modelCollector
        )
    }

    private fun addDoubleHighConeBlock(generator: BlockStateModelGenerator, block: Block, modelName: String) {
        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R0))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R90))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R180))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R270))
                ).coordinate(BlockStateVariantMap.create(Properties.BLOCK_HALF)
                    .register(BlockHalf.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier("zrm", "block/$modelName")))
                    .register(BlockHalf.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, Identifier("zrm", "block/$modelName")))
                )
        )
    }

    private fun addPole(generator: BlockStateModelGenerator, block: Block, name: String) {
        generator.blockStateCollector.accept(createSingletonBlockState(block, Identifier("zrm", "block/$name")))
    }

    private fun addSign(generator: BlockStateModelGenerator, block: SignBlock, name: String) {
        generator.blockStateCollector.accept(createSingletonBlockState(block, Identifier("zrm", "block/$name")))

        signModel.upload(
            block,
            TextureMap()
                .put(TextureKey.FRONT, Identifier("zrm", "block/signs/${block.frontTexture}"))
                .put(TextureKey.BACK, Identifier("zrm","block/signs/${block.backTexture}")),
            generator.modelCollector
        )
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        generator.register(Registry.ModItems.LINKER, Models.GENERATED)

        fun markingItem(name: String): Model {
            return Model(Optional.of(ModId("block/$name")), Optional.empty())
        }
    }
}