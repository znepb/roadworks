package me.znepb.zrm

import me.znepb.zrm.block.*
import me.znepb.zrm.block.entity.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
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

object Registry {
    private val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier("zrm"))
    private val items = mutableListOf<Item>()

    internal fun init() {
        listOf(ModBlocks, ModItems)
        Registry.register(
            ITEM_GROUP, itemGroup, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.zrm.main"))
                .icon{ ItemStack(ModItems.TRAFFIC_CONE) }
                .entries { _, entries ->
                    items.forEach(entries::add)
                }
                .build()
        )
    }

    object ModBlockEntities {
        val POST_BLOCK_ENTITY = registerPostBlockEntities()
        val SIGN_BLOCK_ENTITY = registerSignBlockEntities()
    }

    fun registerPostBlockEntities(): BlockEntityType<PostBlockEntity>? {
        val entity = FabricBlockEntityTypeBuilder.create(::PostBlockEntity, ModBlocks.POST)
        entity.addBlocks(ModBlocks.THICK_POST, ModBlocks.THIN_POST)

        return Registry.register(BLOCK_ENTITY_TYPE, Identifier("zrm", "post_block_entity"), entity.build())
    }

    fun registerSignBlockEntities(): BlockEntityType<SignBlockEntity>? {
        val entity = FabricBlockEntityTypeBuilder.create(::SignBlockEntity, ModBlocks.STOP_SIGN)
        entity.addBlocks(
            ModBlocks.STOP_SIGN_4_WAY,
            ModBlocks.STOP_SIGN_AHEAD,
            ModBlocks.YIELD_SIGN,
            ModBlocks.YIELD_SIGN_AHEAD,
            ModBlocks.SIGNAL_AHEAD,
            ModBlocks.ROAD_WORK_AHEAD
        )

        return Registry.register(BLOCK_ENTITY_TYPE, Identifier("zrm", "sign_block_entity"), entity.build())
    }

    object ModBlocks {
        private fun<T: Block> rBlock(name: String, value: T): T =
            Registry.register(BLOCK, Identifier("zrm", name), value)

        val THICK_POST = rBlock("thick_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thick"))
        val POST = rBlock("post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "medium"))
        val THIN_POST = rBlock("thin_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thin"))

        //

        val TRAFFIC_CONE = rBlock("traffic_cone", TrafficCone(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val CHANNELER = rBlock("channeler", ChannelerBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))
        val DRUM = rBlock("drum", DrumBlock(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))

        //

        val STOP_SIGN = rBlock("stop_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "stop_sign", "back_octagon"))
        val STOP_SIGN_4_WAY = rBlock("stop_sign_4_way", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "4_way", "back_4_way"))
        val STOP_SIGN_AHEAD = rBlock("stop_ahead_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "stop_ahead", "back_diamond"))
        val YIELD_SIGN = rBlock("yield_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "yield", "back_yield"))
        val YIELD_SIGN_AHEAD = rBlock("yield_ahead_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "yield_ahead", "back_diamond"))
        val SIGNAL_AHEAD = rBlock("signal_ahead_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "signal_ahead", "back_diamond"))
        val ROAD_WORK_AHEAD = rBlock("road_work_ahead_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "road_work_ahead", "back_diamond"))

    }

    object ModItems {
        private fun itemSettings(): FabricItemSettings = FabricItemSettings()
        fun<T: Item> rItem(name: String, value: T): T =
            Registry.register(ITEM, Identifier("zrm", name), value).also { items.add(it) }
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

        val STOP_SIGN = rItem(ModBlocks.STOP_SIGN, ::BlockItem, itemSettings())
        val STOP_SIGN_4_WAY = rItem(ModBlocks.STOP_SIGN_4_WAY, ::BlockItem, itemSettings())
        val STOP_SIGN_AHEAD = rItem(ModBlocks.STOP_SIGN_AHEAD, ::BlockItem, itemSettings())
        val YIELD_SIGN = rItem(ModBlocks.YIELD_SIGN, ::BlockItem, itemSettings())
        val YIELD_SIGN_AHEAD = rItem(ModBlocks.YIELD_SIGN_AHEAD, ::BlockItem, itemSettings())
        val SIGNAL_AHEAD = rItem(ModBlocks.SIGNAL_AHEAD, ::BlockItem, itemSettings())
        val ROAD_WORK_AHEAD = rItem(ModBlocks.ROAD_WORK_AHEAD, ::BlockItem, itemSettings())

    }
}