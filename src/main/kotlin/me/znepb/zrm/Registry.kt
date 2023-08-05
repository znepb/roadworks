package me.znepb.zrm

import me.znepb.zrm.block.PostBlock
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries.*
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.text.Text

object Registry {
    private val itemGroup = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier("zrm"))
    private val items = mutableListOf<Item>();

    internal fun init() {
        listOf(ModBlocks, ModItems)
        Registry.register(
            ITEM_GROUP, itemGroup, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.zrm.main"))
                .entries { _, entries ->
                    items.forEach(entries::add)
                }
                .build()
        )
    }

    object ModBlocks {
        fun<T: Block> rBlock(name: String, value: T): T =
            Registry.register(BLOCK, Identifier("zrm", name), value)

        val THICK_POST = rBlock("thick_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thick"))
        val POST = rBlock("post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "medium"))
        val THIN_POST = rBlock("thin_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thin"))
    }

    object ModItems {
        fun itemSettings(): FabricItemSettings = FabricItemSettings()
        fun<T: Item> rItem(name: String, value: T): T =
            Registry.register(ITEM, Identifier("zrm", name), value).also { items.add(it) }
        fun<B: Block, I: Item> rItem(parent:B, supplier: (B, Item.Settings) -> I, settings: Item.Settings = itemSettings()): I {
            val item = Registry.register(ITEM, BLOCK.getId(parent), supplier(parent, settings))
            Item.BLOCK_ITEMS[parent] = item
            items.add(item)
            return item
        }

        val THICK_POST = rItem(ModBlocks.THICK_POST, ::BlockItem, itemSettings())
        val POST = rItem(ModBlocks.POST, ::BlockItem, itemSettings())
        val THIN_POST = rItem(ModBlocks.THIN_POST, ::BlockItem, itemSettings())
    }
}