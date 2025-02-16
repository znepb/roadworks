package me.znepb.roadworks

import dan200.computercraft.api.peripheral.PeripheralLookup
import me.znepb.roadworks.RoadworksMain.ModId
import me.znepb.roadworks.attachment.Attachment
import me.znepb.roadworks.attachment.AttachmentItem
import me.znepb.roadworks.attachment.AttachmentType
import me.znepb.roadworks.cabinet.TrafficCabinet
import me.znepb.roadworks.cabinet.TrafficCabinetBlockEntity
import me.znepb.roadworks.cone.*
import me.znepb.roadworks.container.*
import me.znepb.roadworks.item.Linker
import me.znepb.roadworks.item.SignEditor
import me.znepb.roadworks.item.SignEditorScreenHandler
import me.znepb.roadworks.marking.BasicMarking
import me.znepb.roadworks.marking.OneSideFilledMarking
import me.znepb.roadworks.marking.TMarking
import me.znepb.roadworks.marking.TurnMarking
import me.znepb.roadworks.misc.Catwalk
import me.znepb.roadworks.sign.RoadSignAttachment
import me.znepb.roadworks.sign.SignAttachment
import me.znepb.roadworks.sign.SignAttachmentItem
import me.znepb.roadworks.signal.*
import me.znepb.roadworks.train.CrossingGateArmExtension
import me.znepb.roadworks.train.CrossingGateAttachment
import me.znepb.roadworks.train.TrainBellAttachment
import me.znepb.roadworks.train.TrainSignalAttachment
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
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
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Text
import net.minecraft.util.Identifier

// TODO: Transfer registries into their own class files

object RoadworksRegistry {
    val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier(RoadworksMain.NAMESPACE))
    private val items = mutableListOf<Item>()
    private val creativeInventoryItems = mutableListOf<Item>()

    internal fun init() {
        listOf(ModBlockEntities, ModBlocks, ModItems, ModScreens)

        Registry.register(
            ITEM_GROUP, itemGroup, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.${RoadworksMain.NAMESPACE}.main"))
                .icon{ ItemStack(ModItems.TRAFFIC_CONE) }
                .entries { _, entries ->
                    creativeInventoryItems.forEach { entries.add(it) }
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

        val POST_CONTAINER_BLOCK_ENTITY = registerBlockEntities(
            ::PostContainerBlockEntity,
            listOf(ModBlocks.POST_CONTAINER),
            ModId("container_entity")
        )
        val WALL_CONTAINER_BLOCK_ENTITY = registerBlockEntities(
            ::WallContainerBlockEntity,
            listOf(ModBlocks.WALL_CONTAINER),
            ModId("wall_container_entity")
        )
        val CABINET_BLOCK_ENTITY = registerBlockEntities(
            ::TrafficCabinetBlockEntity,
            listOf(ModBlocks.TRAFFIC_CABINET),
            ModId("traffic_cabinet_block_entity")
        )
    }

    object ModBlocks {
        private fun <T : Block> rBlock(name: String, value: T): T =
            Registry.register(BLOCK, ModId(name), value)

        val POST_CONTAINER = rBlock("post_container", PostContainer(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL)))
        val WALL_CONTAINER = rBlock("wall_container", WallContainer(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL)))

        //

        val TRAFFIC_CONE = rBlock("traffic_cone", TrafficCone(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val CHANNELER = rBlock("channeler", ChannelerBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val DRUM = rBlock("drum", DrumBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val BOLLARD_THIN = rBlock("bollard_thin", BollardThinBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))
        val BOLLARD = rBlock("bollard", BollardBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))
        val BOLLARD_THICK = rBlock("bollard_thick", BollardThickBlock(AbstractBlock.Settings.copy(Blocks.YELLOW_CONCRETE)))

        //

        //

        val TRAFFIC_CABINET =
            rBlock("traffic_cabinet", TrafficCabinet(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))

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

        val CATWALK = rBlock("catwalk", Catwalk())

        val CROSSING_GATE_ARM_EXTENSION = rBlock("crossing_gate_arm_extension", CrossingGateArmExtension())
    }

    object ModItems {
        private fun itemSettings(): FabricItemSettings = FabricItemSettings()

        private fun<T: Item> rItem(name: String, value: T): T =
            rItem(name, value, true)

        private fun<T: Item> rItem(name: String, value: T, addToCreative: Boolean): T =
            Registry.register(ITEM, ModId(name), value).also {
                items.add(it)
                if(addToCreative) creativeInventoryItems.add(it)
            }

        private fun<B: Block, I: Item> rItem(parent:B, supplier: (B, Item.Settings) -> I, settings: Item.Settings =
                itemSettings(), addToCreative: Boolean): I
        {
            val item = Registry.register(ITEM, BLOCK.getId(parent), supplier(parent, settings))
            Item.BLOCK_ITEMS[parent] = item
            items.add(item)
            if(addToCreative) creativeInventoryItems.add(item)
            return item
        }

        private fun<B: Block, I: Item> rItem(parent:B, supplier: (B, Item.Settings) -> I, settings: Item.Settings =
            itemSettings()): I
        {
            return rItem(parent, supplier, settings, true)
        }

        private fun<A: AttachmentType<*>, I: Item> rItem(parent:A, supplier: (A, Item.Settings) -> I, settings: Item.Settings =
            itemSettings(), addToCreative: Boolean): I
        {
            val item = Registry.register(ITEM, ModAttachments.REGISTRY.getId(parent), supplier(parent, settings))
            items.add(item)
            if(addToCreative) creativeInventoryItems.add(item)
            return item
        }

        private fun<A: AttachmentType<*>, I: Item> rItem(parent:A, supplier: (A, Item.Settings) -> I, settings: Item.Settings =
            itemSettings()): I
        {
            return rItem(parent, supplier, settings, true)
        }

        val WRENCH = rItem("wrench", Item(itemSettings()))

        val TRAFFIC_CABINET = rItem(ModBlocks.TRAFFIC_CABINET, ::BlockItem, itemSettings())
        val POST_CONTAINER = rItem("post", PostContainerItem(FabricItemSettings()), false)

        val TRAFFIC_CONE = rItem(ModBlocks.TRAFFIC_CONE, ::BlockItem, itemSettings())
        val CHANNELER = rItem(ModBlocks.CHANNELER, ::BlockItem, itemSettings())
        val DRUM = rItem(ModBlocks.DRUM, ::BlockItem, itemSettings())
        val BOLLARD_THIN = rItem(ModBlocks.BOLLARD_THIN, ::BlockItem, itemSettings())
        val BOLLARD = rItem(ModBlocks.BOLLARD, ::BlockItem, itemSettings())
        val BOLLARD_THICK = rItem(ModBlocks.BOLLARD_THICK, ::BlockItem, itemSettings())

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

        val CATWALK = rItem(ModBlocks.CATWALK, ::BlockItem, itemSettings())

        val LINKER = rItem("linker", Linker(FabricItemSettings()))
        val SIGN_EDITOR = rItem("sign_editor", SignEditor(FabricItemSettings()))

        val SIGN_ATTACHMENT = rItem(ModAttachments.SIGN_ATTACHMENT, ::SignAttachmentItem, itemSettings(), false)
        val ROAD_SIGN_ATTACHMENT = rItem("road_sign", AttachmentItem(ModAttachments.ROAD_SIGN_ATTACHMENT, FabricItemSettings()), false)
        val ROAD_SIGN_WARNING_ATTACHMENT = rItem("road_sign_warning", AttachmentItem(ModAttachments.ROAD_SIGN_ATTACHMENT, FabricItemSettings()), false)
        val PEDESTRIAN_SIGNAL_ATTACHMENT = rItem(ModAttachments.PEDESTRIAN_SIGNAL, ::AttachmentItem, itemSettings())
        val BEACON_ATTACHMENT_RED = rItem(ModAttachments.BEACON_RED, ::AttachmentItem, itemSettings())
        val BEACON_ATTACHMENT_YELLOW = rItem(ModAttachments.BEACON_YELLOW, ::AttachmentItem, itemSettings())
        val BEACON_ATTACHMENT_GREEN = rItem(ModAttachments.BEACON_GREEN, ::AttachmentItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT = rItem(ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT, ::AttachmentItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT = rItem(ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, ::AttachmentItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT = rItem(ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, ::AttachmentItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_STRAIGHT = rItem(ModAttachments.THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_STRAIGHT, ::AttachmentItem, itemSettings())
        val FLASHING_YELLOW_ARROW_SIGNAL_ATTACHMENT = rItem(ModAttachments.FLASHING_YELLOW_ARROW_SIGNAL_ATTACHMENT, ::AttachmentItem, itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT = rItem(ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT, ::AttachmentItem, itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT = rItem(ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT, ::AttachmentItem, itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT_RIGHT = rItem(ModAttachments.FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT_RIGHT, ::AttachmentItem, itemSettings())
        val TRAIN_BELL_ATTACHMENT = rItem(ModAttachments.TRAIN_BELL, ::AttachmentItem, itemSettings())
        val TRAIN_SIGNAL_ATTACHMENT = rItem(ModAttachments.TRAIN_SIGNAL, ::AttachmentItem, itemSettings())
        val CROSSING_GATE_ATTACHMENT = rItem(ModAttachments.CROSSING_GATE, ::AttachmentItem, itemSettings())
        val CROSSING_GATE_ARM_EXTENSION = rItem(ModBlocks.CROSSING_GATE_ARM_EXTENSION, ::BlockItem, itemSettings())
        val BLANKOUT_NO_LEFT_TURN = rItem(ModAttachments.BLANKOUT_NO_LEFT_TURN, ::AttachmentItem, itemSettings())
        val BLANKOUT_NO_RIGHT_TURN = rItem(ModAttachments.BLANKOUT_NO_RIGHT_TURN, ::AttachmentItem, itemSettings())
        val BLANKOUT_NO_TURN_ON_RED = rItem(ModAttachments.BLANKOUT_NO_TURN_ON_RED, ::AttachmentItem, itemSettings())
        val BLANKOUT_NO_LEFT_TURN_TRAIN = rItem(ModAttachments.BLANKOUT_NO_LEFT_TURN_TRAIN, ::AttachmentItem, itemSettings())
        val BLANKOUT_NO_RIGHT_TURN_TRAIN = rItem(ModAttachments.BLANKOUT_NO_RIGHT_TURN_TRAIN, ::AttachmentItem, itemSettings())

    }

    object ModAttachments {
        val REGISTRY_KEY = RegistryKey.ofRegistry<AttachmentType<*>>(Identifier(RoadworksMain.NAMESPACE, "attachments"))
        val REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister()

        val SIGN_ATTACHMENT = Registry.register(REGISTRY, ModId("sign"), AttachmentType.Builder(::SignAttachment).build())
        val ROAD_SIGN_ATTACHMENT = Registry.register(REGISTRY, ModId("road_sign"), AttachmentType.Builder(::RoadSignAttachment).build())

        val PEDESTRIAN_SIGNAL = Registry.register(REGISTRY, ModId("pedestrian_signal"), AttachmentType.Builder(::PedestrianSignalAttachment).build())

        val BEACON_RED = Registry.register(REGISTRY, ModId("beacon_red"), AttachmentType.Builder(BeaconAttachment::Red).build())
        val BEACON_YELLOW = Registry.register(REGISTRY, ModId("beacon_yellow"), AttachmentType.Builder(BeaconAttachment::Yellow).build())
        val BEACON_GREEN = Registry.register(REGISTRY, ModId("beacon_green"), AttachmentType.Builder(BeaconAttachment::Green).build())

        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT = Registry.register(REGISTRY, ModId("three_head_traffic_signal"), AttachmentType.Builder(
            ThreeHeadSignalAttachment::Ball).build())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT = Registry.register(REGISTRY, ModId("three_head_traffic_signal_left"), AttachmentType.Builder(
            ThreeHeadSignalAttachment::Left).build())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT = Registry.register(REGISTRY, ModId("three_head_traffic_signal_right"), AttachmentType.Builder(
            ThreeHeadSignalAttachment::Right).build())
        val THREE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_STRAIGHT = Registry.register(REGISTRY, ModId("three_head_traffic_signal_straight"), AttachmentType.Builder(
            ThreeHeadSignalAttachment::Straight).build())

        val FLASHING_YELLOW_ARROW_SIGNAL_ATTACHMENT = Registry.register(REGISTRY, ModId("flashing_yellow_arrow_signal"), AttachmentType.Builder(
            FourHeadSignalAttachment::FlashingYellowArrowSignal).build())

        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT = Registry.register(REGISTRY, ModId("five_head_traffic_signal_left"), AttachmentType.Builder(
            FiveHeadSignalAttachment::LeftBall).build())
        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_RIGHT = Registry.register(REGISTRY, ModId("five_head_traffic_signal_right"), AttachmentType.Builder(
            FiveHeadSignalAttachment::BallRight).build())
        val FIVE_HEAD_TRAFFIC_SIGNAL_ATTACHMENT_LEFT_RIGHT = Registry.register(REGISTRY, ModId("five_head_traffic_signal_left_right"), AttachmentType.Builder(
            FiveHeadSignalAttachment::LeftRight).build())

        val TRAIN_BELL = Registry.register(REGISTRY, ModId("train_bell"), AttachmentType.Builder(::TrainBellAttachment).build())
        val TRAIN_SIGNAL = Registry.register(REGISTRY, ModId("train_signal"), AttachmentType.Builder(::TrainSignalAttachment).build())
        val CROSSING_GATE = Registry.register(REGISTRY, ModId("crossing_gate"), AttachmentType.Builder(::CrossingGateAttachment).build())

        val BLANKOUT_NO_LEFT_TURN = Registry.register(REGISTRY, ModId("blankout_no_left_turn"), AttachmentType.Builder(BlankoutAttachment::NoLeftTurn).build())
        val BLANKOUT_NO_RIGHT_TURN = Registry.register(REGISTRY, ModId("blankout_no_right_turn"), AttachmentType.Builder(BlankoutAttachment::NoRightTurn).build())
        val BLANKOUT_NO_TURN_ON_RED = Registry.register(REGISTRY, ModId("blankout_no_turn_on_red"), AttachmentType.Builder(BlankoutAttachment::NoTurnOnRed).build())
        val BLANKOUT_NO_LEFT_TURN_TRAIN = Registry.register(REGISTRY, ModId("blankout_no_left_turn_train"), AttachmentType.Builder(BlankoutAttachment::NoLeftTurnTrain).build())
        val BLANKOUT_NO_RIGHT_TURN_TRAIN = Registry.register(REGISTRY, ModId("blankout_no_right_turn_train"), AttachmentType.Builder(BlankoutAttachment::NoRightTurnTrain).build())
    }

    object ModSounds {
        val BELL_SOUND_EVENT = SoundEvent.of(ModId("bell"))
    }
}