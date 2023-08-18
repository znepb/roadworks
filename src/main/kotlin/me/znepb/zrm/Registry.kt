package me.znepb.zrm

import dan200.computercraft.api.peripheral.PeripheralLookup
import me.znepb.zrm.Main.ModId
import me.znepb.zrm.block.*
import me.znepb.zrm.block.cabinet.TrafficCabinet
import me.znepb.zrm.block.cabinet.TrafficCabinetBlockEntity
import me.znepb.zrm.block.cone.*
import me.znepb.zrm.block.marking.BasicMarking
import me.znepb.zrm.block.marking.OneSideFilledMarking
import me.znepb.zrm.block.marking.TurnMarking
import me.znepb.zrm.block.post.PostBlock
import me.znepb.zrm.block.post.PostBlockEntity
import me.znepb.zrm.block.signals.impl.*
import me.znepb.zrm.datagen.TagProvider.Companion.SIGNS
import me.znepb.zrm.item.Linker
import me.znepb.zrm.util.PostThickness
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
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.text.Text

// TODO: Transfer registries into their own class files

object Registry {
    private val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier(Main.NAMESPACE))
    private val items = mutableListOf<Item>()

    internal fun init() {
        listOf(ModBlockEntities, ModBlocks, ModItems)

        Registry.register(
            ITEM_GROUP, itemGroup, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.${Main.NAMESPACE}.main"))
                .icon{ ItemStack(ModItems.TRAFFIC_CONE) }
                .entries { _, entries ->
                    items.forEach(entries::add)
                }
                .build()
        )

        PeripheralLookup.get().registerForBlockEntity({ be, _ -> be.peripheral }, ModBlockEntities.CABINET_BLOCK_ENTITY)
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
            SIGNS,
            ModId("sign_block_entity")
        )
        val CABINET_BLOCK_ENTITY = registerBlockEntities(
            ::TrafficCabinetBlockEntity,
            listOf(ModBlocks.TRAFFIC_CABINET),
            ModId("traffic_cabinet_block_entity")
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

        val STOP_SIGN = rBlock(
            "stop_sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "stop_sign", "back_octagon")
        )
        val STOP_SIGN_4_WAY = rBlock(
            "stop_sign_4_way",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "4_way", "back_4_way")
        )
        val STOP_SIGN_AHEAD = rBlock(
            "stop_ahead_sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "stop_ahead", "back_diamond")
        )
        val YIELD_SIGN =
            rBlock("yield_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "yield", "back_yield"))
        val YIELD_SIGN_AHEAD = rBlock(
            "yield_ahead_sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "yield_ahead", "back_diamond")
        )
        val SIGNAL_AHEAD = rBlock(
            "signal_ahead_sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "signal_ahead", "back_diamond")
        )
        val ROAD_WORK_AHEAD = rBlock(
            "road_work_ahead_sign",
            SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "road_work_ahead", "back_diamond")
        )

        //

        val TRAFFIC_CABINET =
            rBlock("traffic_cabinet", TrafficCabinet(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
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

        val WHITE_INFILL_MARKING = rBlock("marking_white_infill", BasicMarking())
        val WHITE_CENTER_DASH_MARKING = rBlock("marking_white_center_dash", BasicMarking())
        val WHITE_CENTER_MARKING = rBlock("marking_white_center", OneSideFilledMarking())
        val WHITE_CENTER_TURN_MARKING = rBlock("marking_white_turn_center", TurnMarking())
    }

    object ModItems {
        private fun itemSettings(): FabricItemSettings = FabricItemSettings()
        fun<T: Item> rItem(name: String, value: T): T =
            Registry.register(ITEM, ModId(name), value).also { items.add(it) }
        private fun<B: Block, I: Item> rItem(parent:B, supplier: (B, Item.Settings) -> I, settings: Item.Settings = itemSettings()): I {
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

        val STOP_SIGN = rItem(ModBlocks.STOP_SIGN, ::BlockItem, itemSettings())
        val STOP_SIGN_4_WAY = rItem(ModBlocks.STOP_SIGN_4_WAY, ::BlockItem, itemSettings())
        val STOP_SIGN_AHEAD = rItem(ModBlocks.STOP_SIGN_AHEAD, ::BlockItem, itemSettings())
        val YIELD_SIGN = rItem(ModBlocks.YIELD_SIGN, ::BlockItem, itemSettings())
        val YIELD_SIGN_AHEAD = rItem(ModBlocks.YIELD_SIGN_AHEAD, ::BlockItem, itemSettings())
        val SIGNAL_AHEAD = rItem(ModBlocks.SIGNAL_AHEAD, ::BlockItem, itemSettings())
        val ROAD_WORK_AHEAD = rItem(ModBlocks.ROAD_WORK_AHEAD, ::BlockItem, itemSettings())

        val TRAFFIC_CABINET = rItem(ModBlocks.TRAFFIC_CABINET, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_LEFT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_LEFT, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_RIGHT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_RIGHT, ::BlockItem, itemSettings())
        val THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT = rItem(ModBlocks.THREE_HEAD_TRAFFIC_SIGNAL_STRAIGHT, ::BlockItem, itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT = rItem(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_RIGHT, ::BlockItem, itemSettings())
        val FIVE_HEAD_TRAFFIC_SIGNAL_LEFT = rItem(ModBlocks.FIVE_HEAD_TRAFFIC_SIGNAL_LEFT, ::BlockItem, itemSettings())

        val WHITE_INFILL_MARKING = rItem(ModBlocks.WHITE_INFILL_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_DASH_MARKING = rItem(ModBlocks.WHITE_CENTER_DASH_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_MARKING = rItem(ModBlocks.WHITE_CENTER_MARKING, ::BlockItem, itemSettings())
        val WHITE_CENTER_TURN_MARKING = rItem(ModBlocks.WHITE_CENTER_TURN_MARKING, ::BlockItem, itemSettings())

        val LINKER = rItem("linker", Linker(FabricItemSettings()))
    }
}