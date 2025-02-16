package me.znepb.roadworks.datagen

import me.znepb.roadworks.RoadworksMain
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.RoadworksRegistry
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.marking.AbstractMarking.Companion.addBasicMarking
import me.znepb.roadworks.marking.OneSideFilledMarking.Companion.addMarkingWithFilledSides
import me.znepb.roadworks.marking.TMarking.Companion.addTMarking
import me.znepb.roadworks.marking.TurnMarking.Companion.addTurnMarking
import me.znepb.roadworks.signal.SignalLight
import me.znepb.roadworks.train.CrossingGateArmExtension
import me.znepb.roadworks.util.OrientedBlockStateSupplier
import me.znepb.roadworks.util.PostThickness
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.block.Block
import net.minecraft.block.enums.BlockHalf
import net.minecraft.data.client.*
import net.minecraft.data.client.BlockStateModelGenerator.createSingletonBlockState
import net.minecraft.data.client.VariantSettings.Rotation
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    companion object {
        val signModel = Model(
            Optional.of(ModId("block/sign_base")), Optional.empty(),
            TextureKey.FRONT,
            TextureKey.BACK
        )

        val signalModel = Model(Optional.of(ModId("block/signal")), Optional.empty(), TextureKey.TEXTURE)

        val signals = getSignalTextures()

        private fun getSignalTextures(): List<String> {
            val list = mutableListOf<String>()

            SignalLight.entries.forEach {
                list.add("${it.light}_on")
                list.add("${it.light}_off")
            }

            return list
        }
    }

    override fun generateBlockStateModels(generator: BlockStateModelGenerator) {

        addDoubleHighConeBlock(generator, RoadworksRegistry.ModBlocks.CHANNELER, "channeler")
        addDoubleHighConeBlock(generator, RoadworksRegistry.ModBlocks.DRUM, "drum")

        // generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.PEDESTRIAN_SIGNAL, ModId("block/pedestrian_signal")))
        // generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.PEDESTRIAN_BUTTON, ModId("block/pedestrian_button")))

        generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.TRAFFIC_CONE, ModId( "block/traffic_cone")))
        generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.BOLLARD_THIN, ModId("block/bollard_thin")))
        generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.BOLLARD, ModId("block/bollard")))
        generator.blockStateCollector.accept(createSingletonBlockState(RoadworksRegistry.ModBlocks.BOLLARD_THICK, ModId("block/bollard_thick")))

        TexturedModel.ORIENTABLE_WITH_BOTTOM
            .get(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET)
            .textures{ m ->
                m.put(TextureKey.TOP, TextureMap.getSubId(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET, "_top"))
                m.put(TextureKey.BOTTOM, TextureMap.getSubId(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET, "_bottom"))
                m.put(TextureKey.SIDE, TextureMap.getSubId(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET, "_side"))
                m.put(TextureKey.FRONT, TextureMap.getSubId(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET, "_front"))
            }
            .upload(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET, "", generator.modelCollector)

        generator.blockStateCollector.accept(
            OrientedBlockStateSupplier(
                ModId("block/traffic_cabinet")
            ).put(
                MultipartBlockStateSupplier.create(RoadworksRegistry.ModBlocks.TRAFFIC_CABINET)
            )
        )

        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_INFILL_MARKING, "marking_white_infill", true)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_LEFT_MARKING, "marking_white_left_turn_arrow", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_STRAIGHT_MARKING, "marking_white_straight_arrow", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_MARKING, "marking_white_right_turn_arrow", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ONLY_MARKING, "marking_white_only", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_HOV_MARKING, "marking_white_hov_lane", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_RAILROAD_MARKING, "marking_white_rr", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_LEFT_STRAIGHT_MARKING, "marking_white_left_straight_turn_arrows", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_STRAIGHT_MARKING, "marking_white_right_straight_turn_arrows", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_RIGHT_LEFT_MARKING, "marking_white_right_left_turn_arrows", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ARROW_U_TURN_MARKING, "marking_white_u_turn_arrow", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_ZEBRA_CROSSING_MARKING, "marking_white_zebra_crossing", false)
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_YIELD_MARKING, "marking_white_yield", false)

        // White center
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_MARKING, "marking_white_center",
            "marking_white_fill_half", "marking_white_fill_half")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_DASH_MARKING, "marking_white_center_dash")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_TURN_MARKING, "marking_white_turn_center",
            "marking_white_fill_quarter_nw",
            "marking_white_fill_quarter_nw_opposite")
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_THICK, "marking_white_center_thick",
            "marking_white_fill_half", "marking_white_fill_half")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_SHORT, "marking_white_stub_short_center")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_MEDIUM, "marking_white_stub_medium_center")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_CENTER_STUB_LONG, "marking_white_stub_long_center")

        // White edge
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_DASH_MARKING, "marking_white_edge_dash")
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_MARKING,
            "marking_white_edge", "marking_white_edge_filled_left",
            "marking_white_edge_filled_right")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE,
            "marking_white_turn_inside", "marking_white_turn_tight_inside",
            "marking_white_turn_tight_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE,
            "marking_white_turn_outside", "marking_white_turn_wide_inside",
            "marking_white_turn_wide_outside")
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_THICK, "marking_white_edge_thick",
            "marking_white_fill_half", "marking_white_fill_half")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_SHORT_LEFT, "marking_white_stub_short_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_LEFT, "marking_white_stub_medium_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_LONG_LEFT, "marking_white_stub_long_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_SHORT_RIGHT, "marking_white_stub_short_edge_right")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_MEDIUM_RIGHT, "marking_white_stub_medium_edge_right")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.WHITE_EDGE_STUB_LONG_RIGHT, "marking_white_stub_long_edge_right")

        // T
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_CENTER_LONG,
            "marking_white_t_center_long", "marking_white_edge_filled_left",
            "marking_white_fill_quarter_ne_long", "marking_white_fill_quarter_nw_long", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_LEFT_LONG,
            "marking_white_t_left_long", "marking_white_edge_filled_left",
            "marking_white_t_long_left_left", "marking_white_t_long_left_right", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_RIGHT_LONG,
            "marking_white_t_right_long", "marking_white_edge_filled_left",
            "marking_white_t_long_right_left", "marking_white_t_long_right_right", true)

        // T medium
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_CENTER,
            "marking_white_t_center", "marking_white_fill_half",
            "marking_white_fill_quarter_ne", "marking_white_fill_quarter_nw", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_CENTER_LEFT,
            "marking_white_t_left", "marking_white_fill_half",
            "marking_white_t_left_left", "marking_white_t_left_right", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_CENTER_RIGHT,
            "marking_white_t_right", "marking_white_fill_half",
            "marking_white_t_right_left", "marking_white_t_right_right", true)

        // T short
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_CENTER_SHORT,
            "marking_white_t_center_short", "marking_white_edge_filled_right",
            "marking_white_fill_quarter_ne_short", "marking_white_fill_quarter_nw_short", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_SHORT_LEFT,
            "marking_white_t_left_short", "marking_white_edge_filled_right",
            "marking_white_turn_tight_inside_opposite", "marking_white_l_left_short_inside", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.WHITE_T_SHORT_RIGHT,
            "marking_white_t_right_short", "marking_white_edge_filled_right",
            "marking_white_l_right_short_inside", "marking_white_turn_tight_inside", true)

        // L
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_THIN_LEFT, "marking_white_l_thin_left",
            "marking_white_l_left_short_inside",
            "marking_white_l_left_short_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_THIN_RIGHT, "marking_white_l_thin_right",
            "marking_white_l_right_short_inside",
            "marking_white_l_right_short_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_LEFT, "marking_white_l_left",
            "marking_white_l_left_inside",
            "marking_white_l_left_outside")

        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_RIGHT, "marking_white_l_right",
            "marking_white_l_right_inside",
            "marking_white_l_right_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_SHORT_LEFT, "marking_white_l_thin_short_left",
            "marking_white_l_thin_short_left_inside",
            "marking_white_l_thin_short_left_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.WHITE_L_SHORT_RIGHT, "marking_white_l_thin_short_right",
            "marking_white_l_thin_short_right_inside",
            "marking_white_l_thin_short_right_outside")

        ///
        /// Yellow
        ///

        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_INFILL_MARKING, "marking_yellow_infill", true)

        // Yellow center
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_MARKING, "marking_yellow_center",
            "marking_yellow_fill_half", "marking_yellow_fill_half")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_DOUBLE, "marking_yellow_double_center")

        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET, "marking_yellow_offset_center")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_TURN, "marking_yellow_double_center_turn")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_SPLIT_LEFT, "marking_yellow_double_center_split_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_DOUBLE_SPLIT_RIGHT, "marking_yellow_double_center_split_right")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET_INSIDE, "marking_yellow_turn_offset_center_in")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_OFFSET_OUTSIDE, "marking_yellow_turn_offset_center_out")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_R, "marking_yellow_turn_offset_out_center_r")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_L, "marking_yellow_turn_offset_out_center_l")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_R, "marking_yellow_turn_offset_in_center_r")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_L, "marking_yellow_turn_offset_in_center_l")

        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_DASH_MARKING, "marking_yellow_center_dash")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_TURN_MARKING, "marking_yellow_turn_center",
            "marking_yellow_fill_quarter_nw",
            "marking_yellow_fill_quarter_nw_opposite")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_SHORT, "marking_yellow_stub_short_center")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_MEDIUM, "marking_yellow_stub_medium_center")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_CENTER_STUB_LONG, "marking_yellow_stub_long_center")

        // Yellow edge
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_DASH_MARKING, "marking_yellow_edge_dash")
        addMarkingWithFilledSides(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_MARKING,
            "marking_yellow_edge", "marking_yellow_edge_filled_left",
            "marking_yellow_edge_filled_right")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_TURN_MARKING_INSIDE,
            "marking_yellow_turn_inside", "marking_yellow_turn_tight_inside",
            "marking_yellow_turn_tight_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_TURN_MARKING_OUTSIDE,
            "marking_yellow_turn_outside", "marking_yellow_turn_wide_inside",
            "marking_yellow_turn_wide_outside")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_SHORT_LEFT, "marking_yellow_stub_short_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_MEDIUM_LEFT, "marking_yellow_stub_medium_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_LONG_LEFT, "marking_yellow_stub_long_edge_left")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_SHORT_RIGHT, "marking_yellow_stub_short_edge_right")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_MEDIUM_RIGHT, "marking_yellow_stub_medium_edge_right")
        addBasicMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_EDGE_STUB_LONG_RIGHT, "marking_yellow_stub_long_edge_right")

        // T
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_LONG,
            "marking_yellow_t_center_long", "marking_yellow_edge_filled_left",
            "marking_yellow_fill_quarter_ne_long", "marking_yellow_fill_quarter_nw_long", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_LEFT_LONG,
            "marking_yellow_t_left_long", "marking_yellow_edge_filled_left",
            "marking_yellow_t_long_left_left", "marking_yellow_t_long_left_right", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_RIGHT_LONG,
            "marking_yellow_t_right_long", "marking_yellow_edge_filled_left",
            "marking_yellow_t_long_right_left", "marking_yellow_t_long_right_right", true)

        // T medium
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_CENTER,
            "marking_yellow_t_center", "marking_yellow_fill_half",
            "marking_yellow_fill_quarter_ne", "marking_yellow_fill_quarter_nw", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_LEFT,
            "marking_yellow_t_left", "marking_yellow_fill_half",
            "marking_yellow_t_left_left", "marking_yellow_t_left_right", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_RIGHT,
            "marking_yellow_t_right", "marking_yellow_fill_half",
            "marking_yellow_t_right_left", "marking_yellow_t_right_right", true)

        // T short
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_CENTER_SHORT,
            "marking_yellow_t_center_short", "marking_yellow_edge_filled_right",
            "marking_yellow_fill_quarter_ne_short", "marking_yellow_fill_quarter_nw_short", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_SHORT_LEFT,
            "marking_yellow_t_left_short", "marking_yellow_edge_filled_right",
            "marking_yellow_turn_tight_inside_opposite", "marking_yellow_l_left_short_inside", true)
        addTMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_T_SHORT_RIGHT,
            "marking_yellow_t_right_short", "marking_yellow_edge_filled_right",
            "marking_yellow_l_right_short_inside", "marking_yellow_turn_tight_inside", true)

        // L
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_THIN_LEFT, "marking_yellow_l_thin_left",
            "marking_yellow_l_left_short_inside",
            "marking_yellow_l_left_short_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_THIN_RIGHT, "marking_yellow_l_thin_right",
            "marking_yellow_l_right_short_inside",
            "marking_yellow_l_right_short_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_LEFT, "marking_yellow_l_left",
            "marking_yellow_l_left_inside",
            "marking_yellow_l_left_outside")

        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_RIGHT, "marking_yellow_l_right",
            "marking_yellow_l_right_inside",
            "marking_yellow_l_right_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_SHORT_LEFT, "marking_yellow_l_thin_short_left",
            "marking_yellow_l_thin_short_left_inside",
            "marking_yellow_l_thin_short_left_outside")
        addTurnMarking(generator, RoadworksRegistry.ModBlocks.YELLOW_L_SHORT_RIGHT, "marking_yellow_l_thin_short_right",
            "marking_yellow_l_thin_short_right_inside",
            "marking_yellow_l_thin_short_right_outside")

        signals.forEach { addSignal(generator, it) }

        // This block of code is commented out because it conflicts with the item model.
        generator.excludeFromSimpleItemModelGeneration(RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION)
        generator.blockStateCollector.accept(
            VariantsBlockStateSupplier.create(RoadworksRegistry.ModBlocks.CROSSING_GATE_ARM_EXTENSION)
                .coordinate(BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
                    .register(Direction.NORTH, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R0))
                    .register(Direction.EAST, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R90))
                    .register(Direction.SOUTH, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R180))
                    .register(Direction.WEST, BlockStateVariant.create().put(VariantSettings.Y, Rotation.R270))
                )
                .coordinate(BlockStateVariantMap.create(CrossingGateArmExtension.DIRECTION, CrossingGateArmExtension.THICKNESS)
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.HORIZONTAL,
                        PostThickness.THICK,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_post_thick"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.HORIZONTAL,
                        PostThickness.MEDIUM,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_post_medium"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.HORIZONTAL,
                        PostThickness.THIN,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_post_thin"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.HORIZONTAL,
                        PostThickness.NONE,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.VERTICAL,
                        PostThickness.THICK,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_vertical_post_thick"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.VERTICAL,
                        PostThickness.MEDIUM,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_vertical_post_medium"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.VERTICAL,
                        PostThickness.THIN,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_vertical_post_thin"))
                    )
                    .register(
                        CrossingGateArmExtension.CrossingArmDirection.VERTICAL,
                        PostThickness.NONE,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/crossing_gate_arm_vertical"))
                    )
                )
        )
    }

    private fun addSignal(generator: BlockStateModelGenerator, signalName: String) {
        signalModel.upload(
            ModId("block/signal_$signalName"),
            TextureMap()
                .put(TextureKey.TEXTURE, ModId("block/signals/${signalName}")),
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
                    .register(BlockHalf.BOTTOM, BlockStateVariant.create().put(VariantSettings.MODEL, ModId(
                            "block/$modelName")))
                    .register(BlockHalf.TOP, BlockStateVariant.create().put(VariantSettings.MODEL, ModId("block/$modelName")))
                )
        )
    }

    private fun addBlankout(rectangle: Boolean, type: AttachmentType<*>, generator: ItemModelGenerator) {
        val baseBlankoutModelSquare = Model(
            Optional.of(ModId("item/${if(rectangle) "blankout_rectangle_item_base" else "blankout_square_item_base"}")), Optional.empty(),
            TextureKey.TEXTURE
        )
        baseBlankoutModelSquare.upload(
            ModId("item/${RoadworksRegistry.ModAttachments.REGISTRY.getId(type)?.path}"),
            TextureMap()
                .put(TextureKey.TEXTURE, ModId("block/signals/${RoadworksRegistry.ModAttachments.REGISTRY.getId(type)?.path}")),
            generator.writer
        )
    }

    override fun generateItemModels(generator: ItemModelGenerator) {
        generator.register(RoadworksRegistry.ModItems.LINKER, Models.GENERATED)
        generator.register(RoadworksRegistry.ModItems.WRENCH, Models.GENERATED)
        generator.register(RoadworksRegistry.ModItems.SIGN_EDITOR, Models.GENERATED)
        generator.register(RoadworksRegistry.ModItems.ROAD_SIGN_ATTACHMENT, Models.GENERATED)
        generator.register(RoadworksRegistry.ModItems.ROAD_SIGN_WARNING_ATTACHMENT, Models.GENERATED)

        addBlankout(false, RoadworksRegistry.ModAttachments.BLANKOUT_NO_LEFT_TURN, generator)
        addBlankout(false, RoadworksRegistry.ModAttachments.BLANKOUT_NO_RIGHT_TURN, generator)
        addBlankout(true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_TURN_ON_RED, generator)
        addBlankout(true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_LEFT_TURN_TRAIN, generator)
        addBlankout(true, RoadworksRegistry.ModAttachments.BLANKOUT_NO_RIGHT_TURN_TRAIN, generator)


        fun markingItem(name: String): Model {
            return Model(Optional.of(ModId("block/$name")), Optional.empty())
        }
    }
}