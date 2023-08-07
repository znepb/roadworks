package me.znepb.zrm

import me.znepb.zrm.block.TrafficCone
import me.znepb.zrm.block.posts.*
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries.*
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

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

    object ModBlockEntities {
        val POST_BLOCK_ENTITY = registerPostBlockEntities()
    }

    fun registerPostBlockEntities(): BlockEntityType<PostBlockEntity>? {
        val entity = FabricBlockEntityTypeBuilder.create(::PostBlockEntity, ModBlocks.POST)
        entity.addBlocks(ModBlocks.THICK_POST, ModBlocks.THIN_POST)

        return Registry.register(BLOCK_ENTITY_TYPE, Identifier("zrm", "post_block_entity"), entity.build())
    }

    object ModBlocks {
        fun<T: Block> rBlock(name: String, value: T): T =
            Registry.register(BLOCK, Identifier("zrm", name), value)

        val THICK_POST = rBlock("thick_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thick"))
        val POST = rBlock("post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "medium"))
        val THIN_POST = rBlock("thin_post", PostBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL), "thin"))
        val TRAFFIC_CONE = rBlock("traffic_cone", TrafficCone(AbstractBlock.Settings.copy(Blocks.WHITE_CONCRETE)))

        //val STOP_SIGN = rBlock("stop_sign", SignBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICK_WALL)))
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
        val TRAFFIC_CONE = rItem(ModBlocks.TRAFFIC_CONE, ::BlockItem, itemSettings())

        //val STOP_SIGN = rItem(ModBlocks.STOP_SIGN, ::BlockItem, itemSettings())
    }
}