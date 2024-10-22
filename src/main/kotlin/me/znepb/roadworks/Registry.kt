package me.znepb.roadworks

import dan200.computercraft.api.peripheral.PeripheralLookup
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.block.PedestrianButton
import me.znepb.roadworks.block.PedestrianButtonBlockEntity
import me.znepb.roadworks.block.cabinet.TrafficCabinet
import me.znepb.roadworks.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.block.cone.*
import me.znepb.roadworks.block.marking.BasicMarking
import me.znepb.roadworks.block.marking.OneSideFilledMarking
import me.znepb.roadworks.block.marking.TMarking
import me.znepb.roadworks.block.marking.TurnMarking
import me.znepb.roadworks.block.post.PostBlock
import me.znepb.roadworks.block.post.PostBlockEntity
import me.znepb.roadworks.block.sign.*
import me.znepb.roadworks.block.sign.CustomSignBlock
import me.znepb.roadworks.block.sign.CustomSignBlockEntity
import me.znepb.roadworks.block.signals.PedestrianSignal
import me.znepb.roadworks.block.signals.PedestrianSignalBlockEntity
import me.znepb.roadworks.block.signals.impl.*

import me.znepb.roadworks.item.Linker
import me.znepb.roadworks.item.SignEditor
import me.znepb.roadworks.item.SignEditorScreenHandler
import me.znepb.roadworks.util.PostThickness
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries.*
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.resource.featuretoggle.FeatureFlags
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.Identifier

// TODO: Transfer registries into their own class files

object Registry {
    val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier(RoadworksMain.NAMESPACE))
    private val items = mutableListOf<Item>()

    internal fun init() {
        listOf(ModBlockEntities, ModBlocks, ModItems, ModScreens)

        Registry.register(
            ITEM_GROUP, itemGroup, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.${RoadworksMain.NAMESPACE}.main"))
                .icon{ ItemStack(ModItems.TRAFFIC_CONE) }
                .entries { _, entries ->
                    items.filter { it !is SignBlockItem } .forEach { entries.add(it) }
                }
                .build()
        )

        PeripheralLookup.get().registerForBlockEntity({ be, _ -> be.peripheral }, ModBlockEntities.CABINET_BLOCK_ENTITY)
    }

    object ModScreens {
        val SIGN_EDITOR_SCREEN_HANDLER: ScreenHandlerType<SignEditorScreenHandler> = Registry.register(
            SCREEN_HANDLER,
            ModId("sign_editor"),
            ScreenHandlerType(::SignEditorScreenHandler, FeatureFlags.VANILLA_FEATURES)
        )
    }

    object ModBlockEntities {
        private fun <T : BlockEntity> registerBlockEntities(
            factory: Factory<T>,
            objects: List<Block>,
            identifier: Identifier
        ): BlockEntityType<T> {
            val entity = FabricBlockEntityTypeBuilder.create(factory)
            objects.forEach { entity.addBlock(it) }

            return Registry.register(BLOCK_ENTITY_TYPE, identifier, entity.build())
        }

        val POST_BLOCK_ENTITY = registerBlockEntities(
            ::PostBlockEntity,
            listOf(ModBlocks.THIN_POST, ModBlocks.POST, ModBlocks.THICK_POST),
            ModId("post_block_entity")
        )
        val SIGN_BLOCK_ENTITY = registerBlockEntities(
            ::SignBlockEntity,
            listOf(ModBlocks.SIGN),
            ModId("sign_block_entity")
        )
        val CUSTOM_SIGN_BLOCK_ENTITY = registerBlockEntities(
            ::CustomSignBlockEntity,
            listOf(ModBlocks.CUSTOM_SIGN),
            ModId("custom_sign_block_entity")
        )
        val CABINET_BLOCK_ENTITY = registerBlockEntities(
            ::TrafficCabinetBlockEntity,
            listOf(ModBlocks.TRAFFIC_CABINET),
            ModId("traffic_cabinet_block_entity")
        )
        val ONE_HEAD_TRAFFIC_SIGNAL_GREEN_BLOCK_ENTITY = registerBlockEntities(
            ::OneHeadTrafficSignalGreenBlockEntity,
            listOf(ModBlocks.ONE_HEAD_GREEN_TRAFFIC_SIGNAL),
            ModId("green_beacon_block_entity")
        )
        val ONE_HEAD_TRAFFIC_SIGNAL_YELLOW_BLOCK_ENTITY = registerBlockEntities(
            ::OneHeadTrafficSignalYellowBlockEntity,
            listOf(ModBlocks.ONE_HEAD_YELLOW_TRAFFIC_SIGNAL),
            ModId("yellow_beacon_block_entity")
        )
        val ONE_HEAD_TRAFFIC_SIGNAL_RED_BLOCK_ENTITY = registerBlockEntities(
            ::OneHeadTrafficSignalRedBlockEntity,
            listOf(ModBlocks.ONE_HEAD_RED_TRAFFIC_SIGNAL),
            ModId("red_beacon_block_entity")
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_BLOCK_ENTITY = registerBlockEntities(
            ::ThreeHeadTrafficSignalBlockEntity,
            listOf(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL),
            ModId("three_head_traffic_signal_block_entity")
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY = registerBlockEntities(
            ::ThreeHeadTrafficSignalLeftBlockEntity,
            listOf(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT),
            ModId("three_head_traffic_signal_left_block_entity")
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY = registerBlockEntities(
            ::ThreeHeadTrafficSignalRightBlockEntity,
            listOf(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT),
            ModId("three_head_traffic_signal_right_block_entity")
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT_BLOCK_ENTITY = registerBlockEntities(
            ::ThreeHeadTrafficSignalStraightBlockEntity,
            listOf(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT),
            ModId("three_head_traffic_signal_straight_block_entity")
        )
        val FIVE_HEAD_TRAFFIC_SIGNAL_LEFT_BLOCK_ENTITY = registerBlockEntities(
            ::FiveHeadTrafficSignalLeftBlockEntity,
            listOf(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT),
            ModId("five_head_traffic_signal_left_block_entity")
        )
        val FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT_BLOCK_ENTITY = registerBlockEntities(
            ::FiveHeadTrafficSignalRightBlockEntity,
            listOf(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT),
            ModId("five_head_traffic_signal_right_block_entity")
        )
        val PEDESTRIAN_SIGNAL_BLOCK_ENTITY = registerBlockEntities(
            ::PedestrianSignalBlockEntity,
            listOf(ModBlocks.PEDESTRIAN_SIGNAL),
            ModId("pedestrian_signal_block_entity")
        )
        val PEDESTRIAN_BUTTON_BLOCK_ENTITY = registerBlockEntities(
            ::PedestrianButtonBlockEntity,
            listOf(ModBlocks.PEDESTRIAN_BUTTON),
            ModId("pedestrian_button_block_entity")
        )
    }

    object ModBlocks {
        private fun <T : Block> rBlock(name: String, value: T): T =
            Registry.register(BLOCK, ModId(name), value)

        val THICK_POST =
            rBlock("thick_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), PostThickness.THICK))
        val POST = rBlock("post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), PostThickness.MEDIUM))
        val THIN_POST =
            rBlock("thin_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), PostThickness.THIN))

        //

        val TRAFFIC_CONE = rBlock("traffic_cone", TrafficCone(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val CHANNELER = rBlock("channeler", ChannelerBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val DRUM = rBlock("drum", DrumBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val BOLLARD_THIN = rBlock("bollard_thin", BollardThinBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))
        val BOLLARD = rBlock("bollard", BollardBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))
        val BOLLARD_THICK = rBlock("bollard_thick", BollardThickBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))

        //

        val SIGN = rBlock(
            "sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val CUSTOM_SIGN = rBlock(
            "custom_sign",
            CustomSignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )

        //

        val TRAFFIC_CABINET =
            rBlock("traffic_cabinet", TrafficCabinet(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val ONE_HEAD_GREEN_TRAFFIC_SIGNAL = rBlock(
            "green_beacon",
            OneHeadTrafficSignalGreen(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val ONE_HEAD_YELLOW_TRAFFIC_SIGNAL = rBlock(
            "yellow_beacon",
            OneHeadTrafficSignalYellow(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val ONE_HEAD_RED_TRAFFIC_SIGNAL = rBlock(
            "red_beacon",
            OneHeadTrafficSignalRed(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val THREE_HEAD_TRAFFIC_SIGNAL = rBlock(
            "three_head_traffic_signal",
            ThreeHeadTrafficSignal(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_LEFT = rBlock(
            "three_head_traffic_signal_left",
            ThreeHeadTrafficSignalLeft(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_RIGHT = rBlock(
            "three_head_traffic_signal_right",
            ThreeHeadTrafficSignalRight(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT = rBlock(
            "three_head_traffic_signal_straight",
            ThreeHeadTrafficSignalStraight(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val FIVE_HEAD_TRAFFIC_SIGNAL_LEFT = rBlock(
            "five_head_traffic_signal_left",
            FiveHeadTrafficSignalLeft(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT = rBlock(
            "five_head_traffic_signal_right",
            FiveHeadTrafficSignalRight(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL))
        )
        val PEDESTRIAN_SIGNAL = rBlock("pedestrian_signal", PedestrianSignal(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL)))
        val PEDESTRIAN_BUTTON = rBlock("pedestrian_button", PedestrianButton(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL)))

        val YELLOW_INFILL_MARKING = rBlock("marking_yellow_infill", BasicMarking())
        val YELLOW_CENTER_DASH_MARKING = rBlock("marking_yellow_center_dash", BasicMarking())
        val YELLOW_CENTER_MARKING = rBlock("marking_yellow_center", OneSideFilledMarking())
        val YELLOW_CENTER_OFFSET = rBlock("marking_yellow_offset_center", BasicMarking())

        val YELLOW_DOUBLE = rBlock("marking_yellow_double_center", BasicMarking())
        val YELLOW_DOUBLE_TURN = rBlock("marking_yellow_double_center_turn", BasicMarking())
        val YELLOW_DOUBLE_SPLIT_LEFT = rBlock("marking_yellow_double_center_split_left", BasicMarking())
        val YELLOW_DOUBLE_SPLIT_RIGHT = rBlock("marking_yellow_double_center_split_right", BasicMarking())

        val YELLOW_CENTER_OFFSET_INSIDE = rBlock("marking_yellow_turn_offset_center_in", BasicMarking())
        val YELLOW_CENTER_OFFSET_OUTSIDE = rBlock("marking_yellow_turn_offset_center_out", BasicMarking())
        val YELLOW_OFFSET_OUTSIDE_TO_CENTER_R = rBlock("marking_yellow_turn_offset_out_center_r", BasicMarking())
        val YELLOW_OFFSET_OUTSIDE_TO_CENTER_L = rBlock("marking_yellow_turn_offset_out_center_l", BasicMarking())
        val YELLOW_OFFSET_INSIDE_TO_CENTER_R = rBlock("marking_yellow_turn_offset_in_center_r", BasicMarking())
        val YELLOW_OFFSET_INSIDE_TO_CENTER_L = rBlock("marking_yellow_turn_offset_in_center_l", BasicMarking())

        val YELLOW_CENTER_TURN_MARKING = rBlock("marking_yellow_turn_center", TurnMarking())
        val YELLOW_CENTER_STUB_SHORT = rBlock("marking_yellow_stub_short_center", BasicMarking())
        val YELLOW_CENTER_STUB_MEDIUM = rBlock("marking_yellow_stub_medium_center", BasicMarking())
        val YELLOW_CENTER_STUB_LONG = rBlock("marking_yellow_stub_long_center", BasicMarking())

        val YELLOW_EDGE_DASH_MARKING = rBlock("marking_yellow_edge_dash", BasicMarking())
        val YELLOW_EDGE_MARKING = rBlock("marking_yellow_edge", OneSideFilledMarking())
        val YELLOW_EDGE_TURN_MARKING_INSIDE = rBlock("marking_yellow_turn_inside", TurnMarking())
        val YELLOW_EDGE_TURN_MARKING_OUTSIDE = rBlock("marking_yellow_turn_outside", TurnMarking())
        val YELLOW_EDGE_STUB_SHORT_LEFT = rBlock("marking_yellow_stub_short_edge_left", BasicMarking())
        val YELLOW_EDGE_STUB_MEDIUM_LEFT = rBlock("marking_yellow_stub_medium_edge_left", BasicMarking())
        val YELLOW_EDGE_STUB_LONG_LEFT = rBlock("marking_yellow_stub_long_edge_left", BasicMarking())
        val YELLOW_EDGE_STUB_SHORT_RIGHT = rBlock("marking_yellow_stub_short_edge_right", BasicMarking())
        val YELLOW_EDGE_STUB_MEDIUM_RIGHT = rBlock("marking_yellow_stub_medium_edge_right", BasicMarking())
        val YELLOW_EDGE_STUB_LONG_RIGHT = rBlock("marking_yellow_stub_long_edge_right", BasicMarking())

        val YELLOW_T_CENTER_LONG = rBlock("marking_yellow_t_center_long", TMarking())
        val YELLOW_T_LEFT_LONG = rBlock("marking_yellow_t_left_long", TMarking())
        val YELLOW_T_RIGHT_LONG = rBlock("marking_yellow_t_right_long", TMarking())

        val YELLOW_T_CENTER = rBlock("marking_yellow_t_center", TMarking())
        val YELLOW_T_CENTER_LEFT = rBlock("marking_yellow_t_left", TMarking())
        val YELLOW_T_CENTER_RIGHT = rBlock("marking_yellow_t_right", TMarking())

        val YELLOW_T_CENTER_SHORT = rBlock("marking_yellow_t_center_short", TMarking())
        val YELLOW_T_SHORT_LEFT = rBlock("marking_yellow_t_left_short", TMarking())
        val YELLOW_T_SHORT_RIGHT = rBlock("marking_yellow_t_right_short", TMarking())

        val YELLOW_L_THIN_LEFT = rBlock("marking_yellow_l_thin_left", TurnMarking(true))
        val YELLOW_L_THIN_RIGHT = rBlock("marking_yellow_l_thin_right", TurnMarking())
        val YELLOW_L_LEFT = rBlock("marking_yellow_l_left", TurnMarking())
        val YELLOW_L_RIGHT = rBlock("marking_yellow_l_right", TurnMarking(true))
        val YELLOW_L_SHORT_LEFT = rBlock("marking_yellow_l_thin_short_left", TurnMarking())
        val YELLOW_L_SHORT_RIGHT = rBlock("marking_yellow_l_thin_short_right", TurnMarking(true))

        val WHITE_INFILL_MARKING = rBlock("marking_white_infill", BasicMarking())
        val WHITE_ARROW_LEFT_MARKING = rBlock("marking_white_left_turn_arrow", BasicMarking())
        val WHITE_ARROW_STRAIGHT_MARKING = rBlock("marking_white_straight_arrow", BasicMarking())
        val WHITE_ARROW_RIGHT_MARKING = rBlock("marking_white_right_turn_arrow", BasicMarking())
        val WHITE_ARROW_LEFT_STRAIGHT_MARKING = rBlock("marking_white_left_straight_turn_arrows", BasicMarking())
        val WHITE_ARROW_RIGHT_STRAIGHT_MARKING = rBlock("marking_white_right_straight_turn_arrows", BasicMarking())
        val WHITE_ARROW_RIGHT_LEFT_MARKING = rBlock("marking_white_right_left_turn_arrows", BasicMarking())
        val WHITE_ARROW_U_TURN_MARKING = rBlock("marking_white_u_turn_arrow", BasicMarking())
        val WHITE_ONLY_MARKING = rBlock("marking_white_only", BasicMarking())
        val WHITE_HOV_MARKING = rBlock("marking_white_hov_lane", BasicMarking())
        val WHITE_RAILROAD_MARKING = rBlock("marking_white_rr", BasicMarking())
        val WHITE_ZEBRA_CROSSING_MARKING = rBlock("marking_white_zebra_crossing", BasicMarking())
        val WHITE_YIELD_MARKING = rBlock("marking_white_yield", BasicMarking())

        val WHITE_CENTER_DASH_MARKING = rBlock("marking_white_center_dash", BasicMarking())
        val WHITE_CENTER_MARKING = rBlock("marking_white_center", OneSideFilledMarking())
        val WHITE_CENTER_TURN_MARKING = rBlock("marking_white_turn_center", TurnMarking())
        val WHITE_CENTER_THICK = rBlock("marking_white_center_thick", OneSideFilledMarking())
        val WHITE_CENTER_STUB_SHORT = rBlock("marking_white_stub_short_center", BasicMarking())
        val WHITE_CENTER_STUB_MEDIUM = rBlock("marking_white_stub_medium_center", BasicMarking())
        val WHITE_CENTER_STUB_LONG = rBlock("marking_white_stub_long_center", BasicMarking())

        val WHITE_EDGE_DASH_MARKING = rBlock("marking_white_edge_dash", BasicMarking())
        val WHITE_EDGE_MARKING = rBlock("marking_white_edge", OneSideFilledMarking())
        val WHITE_EDGE_TURN_MARKING_INSIDE = rBlock("marking_white_turn_inside", TurnMarking())
        val WHITE_EDGE_TURN_MARKING_OUTSIDE = rBlock("marking_white_turn_outside", TurnMarking())
        val WHITE_EDGE_THICK = rBlock("marking_white_edge_thick", OneSideFilledMarking())
        val WHITE_EDGE_STUB_SHORT_LEFT = rBlock("marking_white_stub_short_edge_left", BasicMarking())
        val WHITE_EDGE_STUB_MEDIUM_LEFT = rBlock("marking_white_stub_medium_edge_left", BasicMarking())
        val WHITE_EDGE_STUB_LONG_LEFT = rBlock("marking_white_stub_long_edge_left", BasicMarking())
        val WHITE_EDGE_STUB_SHORT_RIGHT = rBlock("marking_white_stub_short_edge_right", BasicMarking())
        val WHITE_EDGE_STUB_MEDIUM_RIGHT = rBlock("marking_white_stub_medium_edge_right", BasicMarking())
        val WHITE_EDGE_STUB_LONG_RIGHT = rBlock("marking_white_stub_long_edge_right", BasicMarking())

        val WHITE_T_CENTER_LONG = rBlock("marking_white_t_center_long", TMarking())
        val WHITE_T_LEFT_LONG = rBlock("marking_white_t_left_long", TMarking())
        val WHITE_T_RIGHT_LONG = rBlock("marking_white_t_right_long", TMarking())

        val WHITE_T_CENTER = rBlock("marking_white_t_center", TMarking())
        val WHITE_T_CENTER_LEFT = rBlock("marking_white_t_left", TMarking())
        val WHITE_T_CENTER_RIGHT = rBlock("marking_white_t_right", TMarking())

        val WHITE_T_CENTER_SHORT = rBlock("marking_white_t_center_short", TMarking())
        val WHITE_T_SHORT_LEFT = rBlock("marking_white_t_left_short", TMarking())
        val WHITE_T_SHORT_RIGHT = rBlock("marking_white_t_right_short", TMarking())

        val WHITE_L_THIN_LEFT = rBlock("marking_white_l_thin_left", TurnMarking(true))
        val WHITE_L_THIN_RIGHT = rBlock("marking_white_l_thin_right", TurnMarking())
        val WHITE_L_LEFT = rBlock("marking_white_l_left", TurnMarking())
        val WHITE_L_RIGHT = rBlock("marking_white_l_right", TurnMarking(true))
        val WHITE_L_SHORT_LEFT = rBlock("marking_white_l_thin_short_left", TurnMarking())
        val WHITE_L_SHORT_RIGHT = rBlock("marking_white_l_thin_short_right", TurnMarking(true))
    }

    object ModItems {
        private fun itemSettings(): FabricItemSettings = FabricItemSettings()
        fun<T: Item> rItem(name: String, value: T): T =
            Registry.register(ITEM, ModId(name), value).also { items.add(it) }
        private fun<B: Block, I: Item> rItem(parent:B, supplier: (B, Item.Settings) -> I, settings: Item.Settings =
                itemSettings()): I
        {
            val item = Registry.register(ITEM, BLOCK.getId(parent), supplier(parent, settings))
            Item.BLOCK_ITEMS[parent] = item
            items.add(item)
            return item
        }

        val THICK_POST = rItem(ModBlocks.THICK_POST, ::BlockItem, itemSettings())
        val POST = rItem(ModBlocks.POST, ::BlockItem, itemSettings())
        val THIN_POST = rItem(ModBlocks.THIN_POST, ::BlockItem, itemSettings())

        val TRAFFIC_CONE = rItem(ModBlocks.TRAFFIC_CONE, ::BlockItem, itemSettings())
        val CHANNELER = rItem(ModBlocks.CHANNELER, ::BlockItem, itemSettings())
        val DRUM = rItem(ModBlocks.DRUM, ::BlockItem, itemSettings())
        val BOLLARD_THIN = rItem(ModBlocks.BOLLARD_THIN, ::BlockItem, itemSettings())
        val BOLLARD = rItem(ModBlocks.BOLLARD, ::BlockItem, itemSettings())
        val BOLLARD_THICK = rItem(ModBlocks.BOLLARD_THICK, ::BlockItem, itemSettings())

        val SIGN = rItem(ModBlocks.SIGN, ::SignBlockItem, itemSettings())
        val CUSTOM_SIGN = rItem(ModBlocks.CUSTOM_SIGN, ::CustomSignBlockItem, itemSettings())

        val TRAFFIC_CABINET = rItem(ModBlocks.TRAFFIC_CABINET, ::BlockItem, itemSettings())
        val ONE_HEAD_GREEN_TRAFFIC_SIGNAL = rItem(ModBlocks.ONE_HEAD_GREEN_TRAFFIC_SIGNAL, ::BlockItem, itemSettings())
        val ONE_HEAD_RED_TRAFFIC_SIGNAL = rItem(ModBlocks.ONE_HEAD_RED_TRAFFIC_SIGNAL, ::BlockItem, itemSettings())
        val ONE_HEAD_YELLOW_TRAFFIC_SIGNAL = rItem(ModBlocks.ONE_HEAD_YELLOW_TRAFFIC_SIGNAL, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_LEFT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT, ::BlockItem,
                itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_RIGHT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT, ::BlockItem,
                itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT, ::BlockItem,
                itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT = rItem(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT, ::BlockItem,
                itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_LEFT = rItem(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT, ::BlockItem, itemSettings())
        val PEDESTRIAN_SIGNAL = rItem(ModBlocks.PEDESTRIAN_SIGNAL, ::BlockItem, itemSettings())
        val PEDESTRIAN_BUTTON = rItem(ModBlocks.PEDESTRIAN_BUTTON, ::BlockItem, itemSettings())

        val WHITE_INFILL_MARKING = rItem(ModBlocks.WHITE_INFILL_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_LEFT_MARKING = rItem(ModBlocks.WHITE_ARROW_LEFT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_STRAIGHT_MARKING = rItem(ModBlocks.WHITE_ARROW_STRAIGHT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_RIGHT_MARKING = rItem(ModBlocks.WHITE_ARROW_RIGHT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ONLY_MARKING = rItem(ModBlocks.WHITE_ONLY_MARKING, ::BlockItem, itemSettings())
        val WHITE_HOV_MARKING = rItem(ModBlocks.WHITE_HOV_MARKING, ::BlockItem, itemSettings())
        val WHITE_RAILROAD_MARKING = rItem(ModBlocks.WHITE_RAILROAD_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_LEFT_STRAIGHT_MARKING = rItem(ModBlocks.WHITE_ARROW_LEFT_STRAIGHT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_RIGHT_STRAIGHT_MARKING = rItem(ModBlocks.WHITE_ARROW_RIGHT_STRAIGHT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_RIGHT_LEFT_MARKING = rItem(ModBlocks.WHITE_ARROW_RIGHT_LEFT_MARKING, ::BlockItem, itemSettings())
        val WHITE_ARROW_U_TURN_MARKING = rItem(ModBlocks.WHITE_ARROW_U_TURN_MARKING, ::BlockItem, itemSettings())
        val WHITE_ZEBRA_CROSSING_MARKING = rItem(ModBlocks.WHITE_ZEBRA_CROSSING_MARKING, ::BlockItem, itemSettings())
        val WHITE_YIELD_MARKING = rItem(ModBlocks.WHITE_YIELD_MARKING, ::BlockItem, itemSettings())

        val WHITE_CENTER_DASH_MARKING = rItem(ModBlocks.WHITE_CENTER_DASH_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_MARKING = rItem(ModBlocks.WHITE_CENTER_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_TURN_MARKING = rItem(ModBlocks.WHITE_CENTER_TURN_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_THICK = rItem(ModBlocks.WHITE_CENTER_THICK, ::BlockItem, itemSettings())
        val WHITE_CENTER_STUB_SHORT = rItem(ModBlocks.WHITE_CENTER_STUB_SHORT, ::BlockItem, itemSettings())
        val WHITE_CENTER_STUB_MEDIUM = rItem(ModBlocks.WHITE_CENTER_STUB_MEDIUM, ::BlockItem, itemSettings())
        val WHITE_CENTER_STUB_LONG = rItem(ModBlocks.WHITE_CENTER_STUB_LONG, ::BlockItem, itemSettings())

        val WHITE_EDGE_DASH_MARKING = rItem(ModBlocks.WHITE_EDGE_DASH_MARKING, ::BlockItem, itemSettings())
        val WHITE_EDGE_MARKING = rItem(ModBlocks.WHITE_EDGE_MARKING, ::BlockItem, itemSettings())
        val WHITE_EDGE_TURN_MARKING_INSIDE = rItem(ModBlocks.WHITE_EDGE_TURN_MARKING_INSIDE, ::BlockItem, itemSettings())
        val WHITE_EDGE_TURN_MARKING_OUTSIDE = rItem(ModBlocks.WHITE_EDGE_TURN_MARKING_OUTSIDE, ::BlockItem, itemSettings())
        val WHITE_EDGE_THICK = rItem(ModBlocks.WHITE_EDGE_THICK, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_SHORT_LEFT = rItem(ModBlocks.WHITE_EDGE_STUB_SHORT_LEFT, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_MEDIUM_LEFT = rItem(ModBlocks.WHITE_EDGE_STUB_MEDIUM_LEFT, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_LONG_LEFT = rItem(ModBlocks.WHITE_EDGE_STUB_LONG_LEFT, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_SHORT_RIGHT = rItem(ModBlocks.WHITE_EDGE_STUB_SHORT_RIGHT, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_MEDIUM_RIGHT = rItem(ModBlocks.WHITE_EDGE_STUB_MEDIUM_RIGHT, ::BlockItem, itemSettings())
        val WHITE_EDGE_STUB_LONG_RIGHT = rItem(ModBlocks.WHITE_EDGE_STUB_LONG_RIGHT, ::BlockItem, itemSettings())

        val WHITE_T_CENTER_LONG = rItem(ModBlocks.WHITE_T_CENTER_LONG, ::BlockItem, itemSettings())
        val WHITE_T_LEFT_LONG = rItem(ModBlocks.WHITE_T_LEFT_LONG, ::BlockItem, itemSettings())
        val WHITE_T_RIGHT_LONG = rItem(ModBlocks.WHITE_T_RIGHT_LONG, ::BlockItem, itemSettings())

        val WHITE_T_CENTER = rItem(ModBlocks.WHITE_T_CENTER, ::BlockItem, itemSettings())
        val WHITE_T_CENTER_LEFT = rItem(ModBlocks.WHITE_T_CENTER_LEFT, ::BlockItem, itemSettings())
        val WHITE_T_CENTER_RIGHT = rItem(ModBlocks.WHITE_T_CENTER_RIGHT, ::BlockItem, itemSettings())

        val WHITE_T_CENTER_SHORT = rItem(ModBlocks.WHITE_T_CENTER_SHORT, ::BlockItem, itemSettings())
        val WHITE_T_CENTER_SHORT_LEFT = rItem(ModBlocks.WHITE_T_SHORT_LEFT, ::BlockItem, itemSettings())
        val WHITE_T_CENTER_SHORT_RIGHT = rItem(ModBlocks.WHITE_T_SHORT_RIGHT, ::BlockItem, itemSettings())

        val WHITE_L_THIN_LEFT = rItem(ModBlocks.WHITE_L_THIN_LEFT, ::BlockItem, itemSettings())
        val WHITE_L_THIN_RIGHT = rItem(ModBlocks.WHITE_L_THIN_RIGHT, ::BlockItem, itemSettings())
        val WHITE_L_LEFT = rItem(ModBlocks.WHITE_L_LEFT, ::BlockItem, itemSettings())
        val WHITE_L_RIGHT = rItem(ModBlocks.WHITE_L_RIGHT, ::BlockItem, itemSettings())
        val WHITE_L_SHORT_LEFT = rItem(ModBlocks.WHITE_L_SHORT_LEFT, ::BlockItem, itemSettings())
        val WHITE_L_SHORT_RIGHT = rItem(ModBlocks.WHITE_L_SHORT_RIGHT, ::BlockItem, itemSettings())

        val YELLOW_INFILL_MARKING = rItem(ModBlocks.YELLOW_INFILL_MARKING, ::BlockItem, itemSettings())
        val YELLOW_CENTER_DASH_MARKING = rItem(ModBlocks.YELLOW_CENTER_DASH_MARKING, ::BlockItem, itemSettings())
        val YELLOW_CENTER_MARKING = rItem(ModBlocks.YELLOW_CENTER_MARKING, ::BlockItem, itemSettings())
        val YELLOW_CENTER_OFFSET = rItem(ModBlocks.YELLOW_CENTER_OFFSET, ::BlockItem, itemSettings())

        val YELLOW_DOUBLE = rItem(ModBlocks.YELLOW_DOUBLE, ::BlockItem, itemSettings())
        val YELLOW_DOUBLE_TURN = rItem(ModBlocks.YELLOW_DOUBLE_TURN, ::BlockItem, itemSettings())
        val YELLOW_DOUBLE_SPLIT_LEFT = rItem(ModBlocks.YELLOW_DOUBLE_SPLIT_LEFT, ::BlockItem, itemSettings())
        val YELLOW_DOUBLE_SPLIT_RIGHT = rItem(ModBlocks.YELLOW_DOUBLE_SPLIT_RIGHT, ::BlockItem, itemSettings())
        val YELLOW_CENTER_OFFSET_INSIDE = rItem(ModBlocks.YELLOW_CENTER_OFFSET_INSIDE, ::BlockItem, itemSettings())
        val YELLOW_CENTER_OFFSET_OUTSIDE = rItem(ModBlocks.YELLOW_CENTER_OFFSET_OUTSIDE, ::BlockItem, itemSettings())
        val YELLOW_OFFSET_OUTSIDE_TO_CENTER_R = rItem(ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_R, ::BlockItem, itemSettings())
        val YELLOW_OFFSET_OUTSIDE_TO_CENTER_L = rItem(ModBlocks.YELLOW_OFFSET_OUTSIDE_TO_CENTER_L, ::BlockItem, itemSettings())
        val YELLOW_OFFSET_INSIDE_TO_CENTER_R = rItem(ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_R, ::BlockItem, itemSettings())
        val YELLOW_OFFSET_INSIDE_TO_CENTER_L = rItem(ModBlocks.YELLOW_OFFSET_INSIDE_TO_CENTER_L, ::BlockItem, itemSettings())

        val YELLOW_CENTER_TURN_MARKING = rItem(ModBlocks.YELLOW_CENTER_TURN_MARKING, ::BlockItem, itemSettings())
        val YELLOW_CENTER_STUB_SHORT = rItem(ModBlocks.YELLOW_CENTER_STUB_SHORT, ::BlockItem, itemSettings())
        val YELLOW_CENTER_STUB_MEDIUM = rItem(ModBlocks.YELLOW_CENTER_STUB_MEDIUM, ::BlockItem, itemSettings())
        val YELLOW_CENTER_STUB_LONG = rItem(ModBlocks.YELLOW_CENTER_STUB_LONG, ::BlockItem, itemSettings())

        val YELLOW_EDGE_DASH_MARKING = rItem(ModBlocks.YELLOW_EDGE_DASH_MARKING, ::BlockItem, itemSettings())
        val YELLOW_EDGE_MARKING = rItem(ModBlocks.YELLOW_EDGE_MARKING, ::BlockItem, itemSettings())
        val YELLOW_EDGE_TURN_MARKING_INSIDE = rItem(ModBlocks.YELLOW_EDGE_TURN_MARKING_INSIDE, ::BlockItem, itemSettings())
        val YELLOW_EDGE_TURN_MARKING_OUTSIDE = rItem(ModBlocks.YELLOW_EDGE_TURN_MARKING_OUTSIDE, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_SHORT_LEFT = rItem(ModBlocks.YELLOW_EDGE_STUB_SHORT_LEFT, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_MEDIUM_LEFT = rItem(ModBlocks.YELLOW_EDGE_STUB_MEDIUM_LEFT, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_LONG_LEFT = rItem(ModBlocks.YELLOW_EDGE_STUB_LONG_LEFT, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_SHORT_RIGHT = rItem(ModBlocks.YELLOW_EDGE_STUB_SHORT_RIGHT, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_MEDIUM_RIGHT = rItem(ModBlocks.YELLOW_EDGE_STUB_MEDIUM_RIGHT, ::BlockItem, itemSettings())
        val YELLOW_EDGE_STUB_LONG_RIGHT = rItem(ModBlocks.YELLOW_EDGE_STUB_LONG_RIGHT, ::BlockItem, itemSettings())

        val YELLOW_T_CENTER_LONG = rItem(ModBlocks.YELLOW_T_CENTER_LONG, ::BlockItem, itemSettings())
        val YELLOW_T_LEFT_LONG = rItem(ModBlocks.YELLOW_T_LEFT_LONG, ::BlockItem, itemSettings())
        val YELLOW_T_RIGHT_LONG = rItem(ModBlocks.YELLOW_T_RIGHT_LONG, ::BlockItem, itemSettings())

        val YELLOW_T_CENTER = rItem(ModBlocks.YELLOW_T_CENTER, ::BlockItem, itemSettings())
        val YELLOW_T_CENTER_LEFT = rItem(ModBlocks.YELLOW_T_CENTER_LEFT, ::BlockItem, itemSettings())
        val YELLOW_T_CENTER_RIGHT = rItem(ModBlocks.YELLOW_T_CENTER_RIGHT, ::BlockItem, itemSettings())

        val YELLOW_T_CENTER_SHORT = rItem(ModBlocks.YELLOW_T_CENTER_SHORT, ::BlockItem, itemSettings())
        val YELLOW_T_CENTER_SHORT_LEFT = rItem(ModBlocks.YELLOW_T_SHORT_LEFT, ::BlockItem, itemSettings())
        val YELLOW_T_CENTER_SHORT_RIGHT = rItem(ModBlocks.YELLOW_T_SHORT_RIGHT, ::BlockItem, itemSettings())

        val YELLOW_L_THIN_LEFT = rItem(ModBlocks.YELLOW_L_THIN_LEFT, ::BlockItem, itemSettings())
        val YELLOW_L_THIN_RIGHT = rItem(ModBlocks.YELLOW_L_THIN_RIGHT, ::BlockItem, itemSettings())
        val YELLOW_L_LEFT = rItem(ModBlocks.YELLOW_L_LEFT, ::BlockItem, itemSettings())
        val YELLOW_L_RIGHT = rItem(ModBlocks.YELLOW_L_RIGHT, ::BlockItem, itemSettings())
        val YELLOW_L_SHORT_LEFT = rItem(ModBlocks.YELLOW_L_SHORT_LEFT, ::BlockItem, itemSettings())
        val YELLOW_L_SHORT_RIGHT = rItem(ModBlocks.YELLOW_L_SHORT_RIGHT, ::BlockItem, itemSettings())

        val LINKER = rItem("linker", Linker(FabricItemSettings()))
        val SIGN_EDITOR = rItem("sign_editor", SignEditor(FabricItemSettings()))
    }
}